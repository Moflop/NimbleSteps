package mod.arcomit.parkour.v2.content.behavior.wallrun;

import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.WallData;
import mod.arcomit.parkour.v2.core.sensor.AbstractBoxSensor;
import mod.arcomit.parkour.v2.core.sensor.SensorManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 墙跑核心逻辑
 *
 * @author Arcomit
 */
public class WallRunLogic {
	private static final double VANILLA_SPRINT_SPEED_MULTIPLIER = 2.15; // 原版疾跑的速度倍率
	private static final double WALL_ADHESION_FORCE = 0.1; // 墙面吸附力

	public static void setCollisionDirAndFixedMovementDir(Player player, WallData wallData) {
		Direction wallCollisionDir = findFirstWallCollisionDirection(player);
		wallData.setWallRunCollisionDir3DData(wallCollisionDir.get3DDataValue());// 这个方法只在Enter调用，而进入状态的条件就有左右必须有墙，所以不会为null
		Direction movementDir = player.getDirection();
		wallData.setWallRunMovementDir3DData(movementDir.get3DDataValue());
	}

	/**
	 * 执行墙跑时的物理运动与速度修改
	 */
	public static void useWallRunMovement(Player player, WallData wallData) {
		Direction movementDir = Direction.from3DDataValue(wallData.getWallRunMovementDir3DData());
		Vec3 runDirection = new Vec3(movementDir.getStepX(), 0, movementDir.getStepZ()).normalize();

		double attributeSpeed = player.getAttributeValue(Attributes.MOVEMENT_SPEED);
		double targetSpeed = attributeSpeed * VANILLA_SPRINT_SPEED_MULTIPLIER;

		Direction wallCollisionDirection = Direction.from3DDataValue(wallData.getWallRunCollisionDir3DData());
		if (wallCollisionDirection != null) {
			// 向固定的运动方向移动
			player.setDeltaMovement(runDirection.scale(targetSpeed));

			// 墙面吸附
			Vec3 wallNormal = new Vec3(wallCollisionDirection.getStepX(), 0, wallCollisionDirection.getStepZ());
			Vec3 adhesionForce = wallNormal.scale(WALL_ADHESION_FORCE);
			boolean wasOnGround = player.onGround();
			player.move(MoverType.PLAYER, adhesionForce);
			if (wasOnGround) {
				// 恢复 onGround 状态，防止由于水平贴墙导致的意外状态结束
				player.setOnGround(true);
			}

			// 重置跌落伤害
			player.resetFallDistance();
		}
	}

	public static Direction findFirstWallCollisionDirection(Player player) {
		SensorManager sm = SensorManager.get(player);
		Direction facing = player.getDirection();
		Direction right = facing.getClockWise();
		Direction left = facing.getCounterClockWise();

		boolean hitRight = checkWallCollision(player, sm, right);
		boolean hitLeft = checkWallCollision(player, sm, left);

		if (hitRight && hitLeft) {
			// 内联距离比较：取距离玩家更近的那一侧墙壁
			return getCloserDirection(player, right, left);
		} else if (hitRight) {
			return right;
		} else if (hitLeft) {
			return left;
		}
		return null;
	}

	public static boolean wallCollisionIsValid(Player player, ParkourContext context) {
		Direction wallDir = Direction.from3DDataValue(context.wallData().getWallRunCollisionDir3DData());
		if (wallDir == null) {
			return false;
		}
		SensorManager sensorManager = SensorManager.get(player);
		return checkWallCollision(player, sensorManager, wallDir);
	}

	/**
	 * 在 left 和 right 两个方向中，返回与玩家水平距离更近的那个。
	 * 距离计算逻辑与 {@code DirectionUtils.getClosestDirection} 一致。
	 */
	private static Direction getCloserDirection(Player player, Direction dirA, Direction dirB) {
		BlockPos playerPos = player.blockPosition();
		Vec3 playerVec = player.position();

		double distSqA = horizontalDistanceSq(playerVec, playerPos.relative(dirA));
		double distSqB = horizontalDistanceSq(playerVec, playerPos.relative(dirB));

		return distSqA < distSqB ? dirA : dirB;
	}

	private static double horizontalDistanceSq(Vec3 playerVec, BlockPos blockPos) {
		double dx = playerVec.x - blockPos.getCenter().x;
		double dz = playerVec.z - blockPos.getCenter().z;
		return dx * dx + dz * dz;
	}

	/**
	 * 辅助方法：检测特定方向的墙壁碰撞
	 */
	private static boolean checkWallCollision(Player player, SensorManager sensorManager, Direction dir) {
		// 这里根据 v2 Sensor 命名规则获取
		AbstractBoxSensor headSensor = sensorManager.getSensor("head_wall_" + dir.getName());
		AbstractBoxSensor feetSensor = sensorManager.getSensor("feet_wall_" + dir.getName());

		return headSensor != null && feetSensor != null
			&& headSensor.isColliding(player)
			&& feetSensor.isColliding(player);
	}

	public static boolean isLookingInMovementDirection(Player player, WallData wallData) {
		Direction movementDir = Direction.from3DDataValue(wallData.getWallRunMovementDir3DData());
		Vec3 lookVec = player.getLookAngle();
		float playerYaw = (float) Math.toDegrees(Math.atan2(-lookVec.x, lookVec.z));
		playerYaw = (playerYaw + 360.0f) % 360.0f;

		float targetYaw = movementDir.toYRot();
		targetYaw = (targetYaw + 360.0f) % 360.0f;

		float yawDifference = Math.abs(playerYaw - targetYaw);
		if (yawDifference > 180.0f) {
			yawDifference = 360.0f - yawDifference;
		}
		return yawDifference <= 90.0f;
	}
}