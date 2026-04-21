package mod.arcomit.parkour.v1.event.skills;

import mod.arcomit.parkour.ParkourMod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-01-19
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class TestHandler {

	private static final double CUSTOM_STEP_HEIGHT = 1.5;
	private static final double DETECTION_DISTANCE = 0.3; // 检测前方0.3格

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent. Post event) {
		Player player = event.getEntity();

		// 只在客户端且玩家在地面时处理
		if (!player.level().isClientSide() || !player.onGround()) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		if (mc. player != player) return;

		// 检测空格键按下且有移动输入
//		boolean spacePressed = mc.options.keyJump. isDown();
		boolean hasMovementInput = Math.abs(player.xxa) > 0.001 || Math.abs(player. zza) > 0.001;

		if (hasMovementInput) {
			//tryCustomStep(player);
		}
	}

	private static void tryCustomStep(Player player) {
		Level level = player.level();
		Vec3 movement = player.getDeltaMovement();

		// 获取玩家的移动方向
		Vec3 lookAngle = player.getLookAngle();
		Vec3 forwardDirection = new Vec3(lookAngle.x, 0, lookAngle.z).normalize();

		// 根据玩家输入调整方向
		double forwardInput = player.zza; // W/S
		double strafeInput = player.xxa;  // A/D

		if (Math.abs(forwardInput) < 0.001 && Math.abs(strafeInput) < 0.001) {
			return;
		}

		// 计算实际移动方向
		Vec3 moveDirection = calculateMoveDirection(player, forwardInput, strafeInput);
		if (moveDirection. lengthSqr() < 0.001) {
			return;
		}

		// 检测前方障碍
		AABB playerBB = player.getBoundingBox();
		Vec3 checkPosition = new Vec3(
			playerBB.minX + (playerBB.maxX - playerBB.minX) / 2 + moveDirection.x * DETECTION_DISTANCE,
			playerBB. minY,
			playerBB.minZ + (playerBB.maxZ - playerBB.minZ) / 2 + moveDirection. z * DETECTION_DISTANCE
		);

		// 寻找可以踏上的高度
		double targetHeight = findStepHeight(level, player, checkPosition, playerBB);

		if (targetHeight > 0 && targetHeight <= CUSTOM_STEP_HEIGHT) {
			// 检查目标位置是否安全（不会卡头）
			AABB targetBB = playerBB.move(
				moveDirection.x * DETECTION_DISTANCE,
				targetHeight,
				moveDirection.z * DETECTION_DISTANCE
			);

			if (isSafePosition(level, player, targetBB)) {
				// 应用位置变化
				player.setPos(
					player.getX() + moveDirection.x * DETECTION_DISTANCE,
					player.getY() + targetHeight,
					player.getZ() + moveDirection.z * DETECTION_DISTANCE
				);

				// 取消垂直速度，避免与跳跃冲突
				player.setDeltaMovement(movement. x, 0, movement.z);
				player.hasImpulse = true;
			}
		}
	}

	/**
	 * 计算玩家的移动方向
	 */
	private static Vec3 calculateMoveDirection(Player player, double forward, double strafe) {
		float yaw = player.getYRot();
		double yawRad = Math.toRadians(yaw);

		double x = -Math.sin(yawRad) * forward + Math.cos(yawRad) * strafe;
		double z = Math.cos(yawRad) * forward + Math.sin(yawRad) * strafe;

		Vec3 direction = new Vec3(x, 0, z);
		if (direction.lengthSqr() > 0) {
			return direction.normalize();
		}
		return Vec3.ZERO;
	}

	/**
	 * 寻找可踏上的台阶高度
	 */
	private static double findStepHeight(Level level, Player player, Vec3 checkPos, AABB playerBB) {
		double playerFeetY = playerBB.minY;
		double maxHeight = 0;

		// 从脚底向上检测，每0.1格检测一次
		for (double testY = 0.1; testY <= CUSTOM_STEP_HEIGHT + 0.1; testY += 0.1) {
			BlockPos testPos = new BlockPos(
				(int) Math.floor(checkPos.x),
				(int) Math.floor(playerFeetY + testY),
				(int) Math.floor(checkPos.z)
			);

			BlockState blockState = level.getBlockState(testPos);

			// 如果这个位置有实心方块
			if (! blockState.isAir() && blockState.isSolid()) {
				// 获取方块的精确碰撞箱
				AABB blockBB = blockState.getShape(level, testPos).bounds().move(testPos);

				// 计算需要抬升的高度
				double heightNeeded = blockBB.maxY - playerFeetY;

				if (heightNeeded > maxHeight && heightNeeded <= CUSTOM_STEP_HEIGHT) {
					maxHeight = heightNeeded;
				}
			}
		}

		// 检查目标高度的下一格，确保有落脚点
		if (maxHeight > 0) {
			BlockPos landingPos = new BlockPos(
				(int) Math.floor(checkPos.x),
				(int) Math.floor(playerFeetY + maxHeight),
				(int) Math.floor(checkPos.z)
			);

			BlockState landingBlock = level.getBlockState(landingPos);

			// 确保落脚点是实心的
			if (landingBlock. isAir() || ! landingBlock.isSolid()) {
				return 0;
			}
		}

		return maxHeight;
	}

	/**
	 * 检查位置是否安全（不会卡在方块中）
	 */
	private static boolean isSafePosition(Level level, Player player, AABB boundingBox) {
		// 获取碰撞箱范围内的所有方块位置
		int minX = (int) Math.floor(boundingBox.minX);
		int minY = (int) Math.floor(boundingBox.minY);
		int minZ = (int) Math.floor(boundingBox.minZ);
		int maxX = (int) Math.floor(boundingBox.maxX);
		int maxY = (int) Math.floor(boundingBox.maxY);
		int maxZ = (int) Math.floor(boundingBox.maxZ);

		// 检查是否与任何方块碰撞
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = level.getBlockState(pos);

					if (! state.isAir()) {
						AABB blockBB = state.getShape(level, pos).bounds().move(pos);

						// 如果碰撞箱相交，说明不安全
						if (blockBB.intersects(boundingBox)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}
}