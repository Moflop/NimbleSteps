package mod.arcomit.nimblesteps.event.skills;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.init.NsTags;
import mod.arcomit.nimblesteps.utils.CollisionUtils;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 垂挂处理器。
 *
 * @author Arcomit
 * @since 2026-01-16
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class ArmhangHandler {
	private static final double COLLISION_CHECK_DISTANCE = 0.5; // 碰撞检测距离
	// 底部碰撞检测高度比例，原版玩家站立为1.8格，眼睛高度为1.62，而34%约等于0.6格，确保玩家不会在脚部的位置（一格）触发垂挂
	private static final double BOTTOM_COLLISION_MIN_HEIGHT_RATIO = 0.34;
	private static final double HANGING_POINT_ADHESION_FACTOR = 0.1; // 抓握点吸附力

	private static final double CORNER_OFFSET = 0.05; // 外角转角时的位置偏移
	private static final double EDGE_SEARCH_STEP = 0.05; // 边缘搜索步长
	private static final int MAX_EDGE_SEARCH_ITERATIONS = 10; // 最大边缘搜索迭代次数

	@SubscribeEvent
	public static void tryStartArmhang(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);

		if (!canStartArmhang(player, state)) {
			return;
		}

		Direction facing = player.getDirection();
		if (isClimbableAtDirectionAndPosition(player, player.position(), facing)) {
			startArmhang(state, facing);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void handlerArmhang(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (!state.isArmHanging()) {
			return;
		}

		if (!canArmhang(player, state)) {
			endArmhang(state);
			return;
		}

		applyArmhangMovement(player, state);
	}

	@SubscribeEvent
	public static void checkArmhangInterrupt(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (!state.isArmHanging()) {
			return;
		}

		Direction armhangingDirection = Direction.from3DDataValue(state.getArmHangingDirection());
		boolean isClimbable = isClimbableAtDirectionAndPosition(player, player.position(), armhangingDirection);
		if (!isClimbable || player.isShiftKeyDown()) {
			endArmhang(state);
		}

	}

	// 本地复制左右移动输入控制垂挂时的运动方向，取消原输入防止额外移动
	private static float LOCAL_LEFT_IMPULSE;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void disableMoveWhileArmhanging(MovementInputUpdateEvent event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);

		if (!state.isArmHanging()) {
			return;
		}

		Input input = event.getInput();
		input.forwardImpulse = 0;

		Direction armhangingDirection = Direction.from3DDataValue(state.getArmHangingDirection());
		boolean isFacingArmhangingDirection = player.getDirection() == armhangingDirection;
		if (isFacingArmhangingDirection) {
			LOCAL_LEFT_IMPULSE = input.leftImpulse;
		} else {
			LOCAL_LEFT_IMPULSE = 0;// 没有面向攀爬方向时，不允许左右移动
		}
		input.leftImpulse = 0;
	}

	private static void startArmhang(NimbleStepsState state, Direction direction) {
		state.setArmHanging(true);
		state.setArmHangingDirection(direction.get3DDataValue());
	}

	public static void endArmhang(NimbleStepsState state) {
		state.setArmHanging(false);
		state.resetArmHangingDirection();
	}

	private static void applyArmhangMovement(Player player, NimbleStepsState state) {
		player.setPos(player.getX(), player.getY(), player.getZ());
		player.setDeltaMovement(Vec3.ZERO);

		// 内角旋转检测：检查玩家朝向的方向是否可攀爬
		handleInnerCornerRotation(player, state);

		// 抓握点吸附
		Direction armhangingDirection = Direction.from3DDataValue(state.getArmHangingDirection());
		Vec3 armhangingNormal = new Vec3(armhangingDirection.getStepX(), 0, armhangingDirection.getStepZ());
		Vec3 adhesionForce = armhangingNormal.scale(HANGING_POINT_ADHESION_FACTOR);
		player.move(MoverType.PLAYER, adhesionForce);

		// 左右移动在客户端执行
		if (player instanceof LocalPlayer localPlayer && LOCAL_LEFT_IMPULSE != 0) {
			// 计算左右移动方向（垂直于垂挂方向）
			// 左右方向是垂挂方向的法向量在水平面上的旋转
			Vec3 rightDirection = new Vec3(armhangingDirection.getStepZ(), 0, -armhangingDirection.getStepX());
			Vec3 horizontalMovement = rightDirection.scale(LOCAL_LEFT_IMPULSE).normalize().scale(ServerConfig.armhangMoveSpeed);

			// 记录移动前的位置
			Vec3 beforeMove = player.position();
			player.move(MoverType.PLAYER, horizontalMovement);
			Vec3 afterMove = player.position();

			// 外角旋转检测：检查是否到达无法保持攀爬的位置
			if (!isClimbableAtDirectionAndPosition(player, afterMove, armhangingDirection)) {
				// 尝试外角旋转
				boolean cornerRotated = handleOuterCornerRotation(player, state, afterMove, LOCAL_LEFT_IMPULSE);
				if (!cornerRotated) {
					// 如果无法旋转，尝试迭代寻找安全的边缘位置
					Vec3 safeEdgePosition = findSafeEdgePosition(player, beforeMove, afterMove, armhangingDirection);
					if (safeEdgePosition != null) {
						player.setPos(safeEdgePosition.x, safeEdgePosition.y, safeEdgePosition.z);
					} else {
						// 如果找不到安全位置，则还原位置
						player.setPos(beforeMove.x, beforeMove.y, beforeMove.z);
					}
				}
			}
			localPlayer.sendPosition();
		}

		player.resetFallDistance();
	}


	/**
	 * 处理内角旋转：如果玩家朝向的方向可攀爬且与当前攀爬方向不同，则切换攀爬方向
	 */
	private static void handleInnerCornerRotation(Player player, NimbleStepsState state) {
		Direction playerFacing = player.getDirection();
		Direction currentArmhangDirection = Direction.from3DDataValue(state.getArmHangingDirection());

		// 如果玩家朝向与当前攀爬方向不同，检查是否可以切换到朝向的墙
		if (playerFacing != currentArmhangDirection) {
			if (isClimbableAtDirectionAndPosition(player, player.position(), playerFacing)) {
				state.setArmHangingDirection(playerFacing.get3DDataValue());
			}
		}
	}

	/**
	 * 处理外角旋转：当玩家移动到无法保持攀爬的位置时，尝试转角
	 *
	 * @param player 玩家实体
	 * @param state 玩家状态
	 * @param afterMove 移动后的位置
	 * @param leftImpulse 左右移动输入（正值为右，负值为左）
	 * @return 是否成功转角
	 */
	private static boolean handleOuterCornerRotation(Player player, NimbleStepsState state,
							 Vec3 afterMove, float leftImpulse) {
		Direction currentDirection = Direction.from3DDataValue(state.getArmHangingDirection());

		// 向原攀爬方向偏移0.05生成新坐标
		Vec3 directionOffset = new Vec3(currentDirection.getStepX(), 0, currentDirection.getStepZ()).scale(CORNER_OFFSET);
		Vec3 cornerPosition = afterMove.add(directionOffset);

		// 检查转角位置是否有足够空间
		double halfWidth = player.getBbWidth() / 2;
		AABB cornerBox = new AABB(
			cornerPosition.x - halfWidth,
			cornerPosition.y,
			cornerPosition.z - halfWidth,
			cornerPosition.x + halfWidth,
			cornerPosition.y + player.getBbHeight(),
			cornerPosition.z + halfWidth);
		if (CollisionUtils.isBlockCollision(player.level(), cornerBox, BlockTags.AIR)) {
			return false;
		}

		// 确定要检测的新方向
		Direction newDirection;
		if (leftImpulse > 0) {
			// 向左移动时，检测顺时针方向（左转）
			newDirection = currentDirection.getClockWise();
		} else {
			// 向右移动时，检测逆时针方向（右转）
			newDirection = currentDirection.getCounterClockWise();
		}

		// 检查新方向是否可攀爬
		if (isClimbableAtDirectionAndPosition(player, cornerPosition, newDirection)) {
			// 设置玩家到转角位置并切换攀爬方向
			player.setPos(cornerPosition.x, cornerPosition.y, cornerPosition.z);
			state.setArmHangingDirection(newDirection.get3DDataValue());
			return true;
		}

		return false;
	}

	/**
	 * 迭代寻找安全的边缘位置：从起始位置向目标位置逐步搜索，找到最后一个可以攀爬的安全位置
	 *
	 * @param player 玩家实体
	 * @param startPos 起始位置（移动前的安全位置）
	 * @param endPos 目标位置（移动后的不安全位置）
	 * @param direction 当前攀爬方向
	 * @return 找到的安全边缘位置，如果找不到则返回null
	 */
	private static Vec3 findSafeEdgePosition(Player player,
						 Vec3 startPos, Vec3 endPos, Direction direction) {
		// 计算移动方向向量
		Vec3 moveDirection = endPos.subtract(startPos);
		double totalDistance = moveDirection.length();

		// 如果移动距离太小，不进行搜索
		if (totalDistance < EDGE_SEARCH_STEP) {
			return null;
		}

		// 归一化移动方向
		Vec3 normalizedDirection = moveDirection.normalize();

		// 迭代搜索：从起始位置开始，逐步向目标位置移动
		Vec3 lastSafePosition = null;
		for (int i = 1; i <= MAX_EDGE_SEARCH_ITERATIONS; i++) {
			double searchDistance = EDGE_SEARCH_STEP * i;

			// 如果搜索距离超过总距离，停止搜索
			if (searchDistance >= totalDistance) {
				break;
			}

			// 计算当前搜索位置
			Vec3 searchPosition = startPos.add(normalizedDirection.scale(searchDistance));

			// 检查该位置是否可以攀爬
			if (isClimbableAtDirectionAndPosition(player, searchPosition, direction)) {
				lastSafePosition = searchPosition;
			} else {
				// 找到第一个不安全的位置，停止搜索
				break;
			}
		}

		return lastSafePosition;
	}

	private static boolean canStartArmhang(Player player, NimbleStepsState state) {
		boolean isFalling = player.fallDistance > 0f;
		return ServerConfig.enableArmhang
			&& !state.isArmHanging()
			&& !player.onGround()
			&& state.isHasJumped()
			&& isFalling
			&& !player.isShiftKeyDown()
			&& canArmhang(player, state);
	}

	private static boolean canArmhang(Player player, NimbleStepsState state) {
		return !state.isCrawling()
			&& !state.isSliding()
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	private static boolean isClimbableAtDirectionAndPosition(Player player, Vec3 position, Direction direction) {
		return isClimbableAtSpecificHeight(player, position, direction, player.getEyeHeight())
			|| isClimbableAtSpecificHeight(player, position, direction, player.getBbHeight());
	}

	private static boolean isClimbableAtSpecificHeight(
		Player player, Vec3 position, Direction direction, double baseHeight) {
		double halfWidth = player.getBbWidth() / 2;
		double height = player.getBbHeight();
		double topOffset = height - player.getEyeHeight();

		double minX = position.x - halfWidth;
		double maxX = position.x + halfWidth;
		double minZ = position.z - halfWidth;
		double maxZ = position.z + halfWidth;

		double baseY = position.y + baseHeight;

		// 顶部检测：必须有抓握点，没有方块阻挡
		AABB topBox = new AABB(minX, baseY, minZ, maxX, baseY + topOffset, maxZ);
		// 底部检测：必须有方块支撑点
		AABB bottomBox = new AABB(minX, baseY, minZ, maxX, baseY - (height * BOTTOM_COLLISION_MIN_HEIGHT_RATIO), maxZ);

		return CollisionUtils.isCollidingWithBlockInDirection(
			player.level(),
			bottomBox,
			direction,
			COLLISION_CHECK_DISTANCE,
			NsTags.Blocks.SCAFFOLDING_BLOCKS)
			&& !CollisionUtils.isCollidingWithBlockInDirection(
			player.level(),
			topBox,
			direction,
			COLLISION_CHECK_DISTANCE,
			NsTags.Blocks.SCAFFOLDING_BLOCKS);
	}
}

