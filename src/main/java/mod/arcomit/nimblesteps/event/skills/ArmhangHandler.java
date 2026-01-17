package mod.arcomit.nimblesteps.event.skills;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.init.NsTags;
import mod.arcomit.nimblesteps.utils.CollisionUtils;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.client.player.Input;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 墙面垂挂处理器。
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class ArmhangHandler {

	private static final Vec3 UP_VECTOR = new Vec3(0, 1, 0);

	/** 玩家面向墙面的最小点积阈值（夹角约60度以内）。 */
	private static final double FACING_WALL_DOT_THRESHOLD = 0.5;

	private static final double EPSILON = 1.0E-5;
	private static final double WALL_ADHESION_FORCE = 0.2;
	private static final double MOVEMENT_PROBE_DISTANCE = 0.2;
	private static final double INPUT_PROBE_DISTANCE = 0.05;
	private static final double COLLISION_CHECK_DISTANCE = 0.5;
	private static final double BOTTOM_COLLISION_HEIGHT = 0.62;
	private static final double boxOffset = 0.05;

	// ==================== 事件处理 ====================

	/**
	 * 处理玩家在垂挂状态下的输入控制。
	 *
	 * <p>在客户端限制玩家输入，确保只能进行沿墙移动。
	 */
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onMovementInput(MovementInputUpdateEvent event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);

		if (!state.isArmHanging()) {
			return;
		}

		Input input = event.getInput();
		Direction clingDirection = Direction.from3DDataValue(state.getArmHangingDirection());
		Vec3 clingDirectionVector = Vec3.atLowerCornerOf(clingDirection.getNormal());

		// 如果玩家没有面向墙面，阻止所有输入
		if (!isFacingWall(player, clingDirectionVector)) {
			blockAllMovementInput(input);
		} else {
			// 限制输入为沿墙移动
			restrictToWallMovement(player, input, clingDirection, clingDirectionVector);
		}
	}

	/**
	 * 处理垂挂状态的逻辑刻。
	 *
	 * <p>在服务端和客户端同时运行，处理垂挂的开始、维持和结束。
	 */
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);

		if (state.isArmHanging()) {
			handleArmHang(player, state);
		} else {
			tryStartArmHang(player, state);
		}
	}

	// ==================== 垂挂状态管理 ====================

	/**
	 * 处理维持垂挂状态。
	 *
	 * @param player 玩家实体
	 * @param state 玩家的 NimbleSteps 状态
	 */
	private static void handleArmHang(Player player, NimbleStepsState state) {
		// 检查是否应该停止垂挂
		if (shouldStopArmHang(player)) {
			stopArmHang(state);
			return;
		}

		Direction clingDirection = Direction.from3DDataValue(state.getArmHangingDirection());

		// 检查是否仍然可以垂挂在当前墙面
		if (!canArmHangOnWall(player, clingDirection)) {
			stopArmHang(state);
			return;
		}

		// 固定玩家Y坐标，阻止掉落
		player.setPos(player.getX(), state.getArmHangingY(), player.getZ());
		player.setDeltaMovement(Vec3.ZERO);
		player.resetFallDistance();

		// 应用沿墙移动
		applyWallMovement(player, state, clingDirection);
	}

	/**
	 * 尝试开始垂挂。
	 *
	 * @param player 玩家实体
	 * @param state 玩家的 NimbleSteps 状态
	 */
	private static void tryStartArmHang(Player player, NimbleStepsState state) {
		// 条件检查：必须在空中、下落中、已跳跃、未潜行
		if (player.onGround() || player.isShiftKeyDown() || !state.isHasJumped()) {
			return;
		}

		// 必须是下落状态
		if (player.getDeltaMovement().y < 0) {
			Direction facingDirection = player.getDirection();
			if (canArmHangOnWall(player, facingDirection)) {
				startArmHang(player, state, facingDirection);
			}
		}
	}

	private static void startArmHang(Player player, NimbleStepsState state, Direction direction) {
		state.setArmHanging(true);
		state.setArmHangingDirection(direction.get3DDataValue());
		state.setArmHangingY(player.getY());
		player.setDeltaMovement(Vec3.ZERO);
	}

	private static void stopArmHang(NimbleStepsState state) {
		state.setArmHanging(false);
		state.setArmHangingDirection(-1);
	}

	private static boolean shouldStopArmHang(Player player) {
		return player.isShiftKeyDown() || player.onGround();
	}

	// ==================== 输入控制 ====================

	private static void blockAllMovementInput(Input input) {
		input.forwardImpulse = 0;
		input.leftImpulse = 0;
	}

	private static float LEFT_IMPULSE;
	private static void restrictToWallMovement(
		Player player, Input input, Direction clingDirection, Vec3 clingDirectionVector) {
		// 禁止前后移动
		input.forwardImpulse = 0;

		System.out.println(input.leftImpulse);
		LEFT_IMPULSE = input.leftImpulse;
		input.leftImpulse = 0;
		// 处理左右移动输入
		if (input.leftImpulse != 0) {
			boolean movingLeft = input.leftImpulse > 0;
			Vec3 strafeVector = getWallStrafeVector(clingDirectionVector, input.leftImpulse);
			Vec3 probeVector = strafeVector.normalize().scale(INPUT_PROBE_DISTANCE);

			// 检查是否可以转角或继续沿墙移动
			if (!tryTurn(player, clingDirection, movingLeft)
				&& !canArmHangAt(player, player.position().add(probeVector), clingDirection)) {
				input.leftImpulse = 0;
			}
		}
	}

	// ==================== 墙面移动逻辑 ====================

	private static void applyWallMovement(
		Player player, NimbleStepsState state, Direction clingDirection) {
		Vec3 clingDirectionVector = Vec3.atLowerCornerOf(clingDirection.getNormal());
		Vec3 adhesionForce = clingDirectionVector.scale(WALL_ADHESION_FORCE);

		// 1. 如果玩家未面向墙面，尝试转角 (保持原有逻辑)
		if (!isFacingWall(player, clingDirectionVector)) {
			if (trySwitchToCornerWall(player, state, clingDirection)) {
				return;
			}
			player.setDeltaMovement(adhesionForce);
			return;
		}

		// 2. 获取横向移动输入
		double strafeInput = LEFT_IMPULSE;
		if (Math.abs(strafeInput) <= EPSILON) {
			player.setDeltaMovement(adhesionForce);
			return;
		}

		// 3. 计算预期的沿墙移动向量
		Vec3 strafeMovement =
			getWallStrafeVector(clingDirectionVector, strafeInput)
				.scale(ServerConfig.wallClingMoveSpeed);

		// 4. 尝试转角 (如果遇到内角/外角，优先转弯，保持流畅性)
		if (performTurn(player, state, clingDirection, strafeInput > 0)) {
			return;
		}

		// 5. 【核心改进】边缘保护检测
		// 检查完整位移是否会导致脱手（悬空）
		Vec3 targetPos = player.position().add(strafeMovement);
		if (!canArmHangAt(player, targetPos, clingDirection)) {
			// 如果完整位移不安全，则启用“迭代逼近”寻找边缘
			strafeMovement = findSafeEdgePosition(player, strafeMovement, clingDirection);
		}

		// 应用最终计算出的位移（可能被削减过，正好停在边缘）
		player.setDeltaMovement(strafeMovement.add(adhesionForce));
	}

	/**
	 * 迭代寻找安全的边缘位置。
	 * <p>
	 * 类似原版潜行的逻辑：如果原本的移动会导致玩家掉下墙壁，
	 * 此方法会尝试逐步减小移动距离，直到找到一个既靠近边缘又安全的位置。
	 */
	private static Vec3 findSafeEdgePosition(Player player, Vec3 originalMove, Direction clingDirection) {
		Vec3 normalizedDir = originalMove.normalize();
		double totalDistance = originalMove.length();

		// 步进值：越小越精准，但性能开销越大。0.05 是原版潜行的经验值。
		double increment = 0.05;

		// 从原定距离开始，反向递减，直到找到安全点
		// 我们使用 currentDist > 0 来防止死循环，并处理极小距离
		for (double currentDist = totalDistance; currentDist > 0; currentDist -= increment) {

			// 如果剩余距离极小，直接视为 0 (防止抖动)
			if (currentDist < EPSILON) {
				return Vec3.ZERO;
			}

			// 构造试探性向量
			Vec3 trialMove = normalizedDir.scale(currentDist);
			Vec3 trialPos = player.position().add(trialMove);

			// 检查在这个位置是否还能挂住墙
			if (canArmHangAt(player, trialPos, clingDirection)) {
				return trialMove; // 找到了！这是离边缘最近的安全点
			}
		}

		// 如果连一点点距离都移动不了，则完全停止
		return Vec3.ZERO;
	}

	private static boolean trySwitchToCornerWall(
		Player player, NimbleStepsState state, Direction currentClingDirection) {
		// 尝试顺时针方向
		Direction clockwise = currentClingDirection.getClockWise();
		if (canArmHangAt(player, player.position(), clockwise)
			&& isFacingWall(player, Vec3.atLowerCornerOf(clockwise.getNormal()))) {
			state.setArmHangingDirection(clockwise.get3DDataValue());
			return true;
		}

		// 尝试逆时针方向
		Direction counterClockwise = currentClingDirection.getCounterClockWise();
		if (canArmHangAt(player, player.position(), counterClockwise)
			&& isFacingWall(player, Vec3.atLowerCornerOf(counterClockwise.getNormal()))) {
			state.setArmHangingDirection(counterClockwise.get3DDataValue());
			return true;
		}

		return false;
	}

	private static boolean tryTurn(Player player, Direction clingDirection, boolean movingLeft) {
		Direction targetDirection =
			movingLeft ? clingDirection.getCounterClockWise() : clingDirection.getClockWise();
		return canArmHangAt(player, player.position(), targetDirection);
	}

	private static boolean performTurn(
		Player player, NimbleStepsState state, Direction clingDirection, boolean movingLeft) {
		Direction targetDirection =
			movingLeft ? clingDirection.getCounterClockWise() : clingDirection.getClockWise();

		// 必须面向目标墙面才能转向
		if (!isFacingWall(player, Vec3.atLowerCornerOf(targetDirection.getNormal()))) {
			return false;
		}

		if (canArmHangAt(player, player.position(), targetDirection)) {
			state.setArmHangingDirection(targetDirection.get3DDataValue());
			return true;
		}
		return false;
	}

	private static Vec3 getWallStrafeVector(Vec3 clingDirectionVector, double magnitude) {
		Vec3 wallLeftVector = UP_VECTOR.cross(clingDirectionVector).normalize();
		return wallLeftVector.scale(magnitude);
	}

	// ==================== 条件检查 ====================

	private static boolean isFacingWall(Player player, Vec3 clingDirectionVector) {
		return player.getLookAngle().dot(clingDirectionVector) > FACING_WALL_DOT_THRESHOLD;
	}

	private static boolean canArmHangOnWall(Player player, Direction direction) {
		if (!ServerConfig.enableWallCling || isPlayerStateInvalid(player)) {
			return false;
		}
		return canArmHangAt(player, player.position(), direction);
	}

	private static boolean isPlayerStateInvalid(Player player) {
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		return state.isSliding()
			|| state.isCrawling()
			|| player.onClimbable()
			|| player.isInWater()
			|| player.isInLava()
			|| player.isSwimming()
			|| !PlayerStateUtils.isAbleToAction(player);
	}

	private static boolean canArmHangAt(Player player, Vec3 position, Direction direction) {
		return hasValidWallCollision(player, position, direction);
	}

	private static boolean hasValidWallCollision(Player player, Vec3 position, Direction direction) {
		return checkCollisionAtHeight(player, position, direction, player.getEyeHeight())
			|| checkCollisionAtHeight(player, position, direction, player.getBbHeight());
	}

	private static boolean checkCollisionAtHeight(
		Player player, Vec3 position, Direction direction, double baseHeight) {
		double halfWidth = player.getBbWidth() * 0.5;
		double topOffset = player.getBbHeight() - player.getEyeHeight();

		double minX = position.x - halfWidth;
		double maxX = position.x + halfWidth;
		double minZ = position.z - halfWidth;
		double maxZ = position.z + halfWidth;

		double baseY = position.y + baseHeight;

		// 顶部碰撞箱：确保顶部有空间可供手臂抓握
		AABB topBox =
			new AABB(
				minX,
				baseY,
				minZ,
				maxX,
				baseY + topOffset,
				maxZ);
		// 底部碰撞箱：检测墙面
		AABB bottomBox =
			new AABB(
				minX,
				baseY,
				minZ,
				maxX,
				baseY - BOTTOM_COLLISION_HEIGHT,
				maxZ);

		return CollisionUtils.isCollidingWithBlockInDirection(
			player.level(),
			bottomBox,
			direction,
			COLLISION_CHECK_DISTANCE,
			NsTags.Blocks.SCAFFOLDING_BLOCKS)
			&&
			!CollisionUtils.isCollidingWithBlockInDirection(
				player.level(),
				topBox,
				direction,
				COLLISION_CHECK_DISTANCE,
				NsTags.Blocks.SCAFFOLDING_BLOCKS);
	}
}