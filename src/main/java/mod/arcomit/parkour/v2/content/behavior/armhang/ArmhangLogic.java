package mod.arcomit.parkour.v2.content.behavior.armhang;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.CollisionUtils;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.WallData;
import mod.arcomit.parkour.v2.core.sensor.SensorManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 垂挂逻辑处理类。
 *
 * @author Arcomit
 */
public class ArmhangLogic {
	private static final double HANGING_POINT_ADHESION_FACTOR = 0.1; // 抓握点吸附力
	private static final double CORNER_OFFSET = 0.05; // 外角转角时的位置偏移
	private static final double EDGE_SEARCH_STEP = 0.05; // 边缘搜索步长
	private static final int MAX_EDGE_SEARCH_ITERATIONS = 10; // 最大边缘搜索迭代次数

	/**
	 * 检查玩家在特定方向是否可以垂挂。
	 */
	public static boolean isClimbableAtDirection(Player player, Direction direction) {
		SensorManager sensorManager = SensorManager.get(player);

		// 1. 基于眼睛高度的检测
		boolean eyeGrip = sensorManager.getSensor("armhang_grip_eye" + direction.getName()).isColliding(player);
		boolean eyeSupport = sensorManager.getSensor("armhang_support_eye" + direction.getName()).isColliding(player);

		// 2. 基于碰撞箱顶端高度的检测
		boolean topGrip = sensorManager.getSensor("armhang_grip_top" + direction.getName()).isColliding(player);
		boolean topSupport = sensorManager.getSensor("armhang_support_top" + direction.getName()).isColliding(player);

		// 有支撑点且无抓握点遮挡即可攀爬
		return (eyeSupport && !eyeGrip) || (topSupport && !topGrip);
	}

	/**
	 * 判断当前环境是否允许垂挂
	 */
	public static boolean canStartArmhang(Player player) {
		ParkourContext context = ParkourContext.get(player);
		boolean isFalling = player.fallDistance > 0f;

		return ServerConfig.enableArmhang
			&& !player.onGround()
			&& context.jumpData().isJumped()
			&& isFalling
			&& !player.isShiftKeyDown()
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	/**
	 * 处理垂挂物理运动
	 */
	public static void applyArmhangMovement(Player player, ParkourContext context) {
		WallData wallData = context.wallData();
		player.setPos(player.getX(), player.getY(), player.getZ());
		player.setDeltaMovement(Vec3.ZERO);

		Direction armhangingDirection = Direction.from3DDataValue(wallData.getArmHangingDirection());

		// 内角旋转检测
		handleInnerCornerRotation(player, wallData);

		// 抓握点吸附
		Vec3 armhangingNormal = new Vec3(armhangingDirection.getStepX(), 0, armhangingDirection.getStepZ());
		Vec3 adhesionForce = armhangingNormal.scale(HANGING_POINT_ADHESION_FACTOR);
		player.move(MoverType.PLAYER, adhesionForce);

		// 左右移动在客户端执行，利用 Input 判定
		if (player instanceof LocalPlayer localPlayer) {
			float leftImpulse = localPlayer.input.leftImpulse;
			if (leftImpulse != 0) {
				handleHorizontalMovement(localPlayer, wallData, armhangingDirection, leftImpulse);
				// 取消原生移动输入，防止多余的走动
				localPlayer.input.leftImpulse = 0;
			}
			localPlayer.input.forwardImpulse = 0;
		}

		player.resetFallDistance();
	}

	private static void handleInnerCornerRotation(Player player, WallData wallData) {
		Direction playerFacing = player.getDirection();
		Direction currentArmhangDirection = Direction.from3DDataValue(wallData.getArmHangingDirection());

		if (playerFacing != currentArmhangDirection) {
			if (isClimbableAtDirection(player, playerFacing)) {
				wallData.setArmHangingDirection(playerFacing.get3DDataValue());
			}
		}
	}

	private static void handleHorizontalMovement(LocalPlayer player, WallData wallData, Direction armhangingDirection, float leftImpulse) {
		boolean isFacingArmhangingDirection = player.getDirection() == armhangingDirection;

		Vec3 rightDirection = new Vec3(armhangingDirection.getStepZ(), 0, -armhangingDirection.getStepX());
		Vec3 horizontalMovement = rightDirection.scale(leftImpulse).normalize().scale(ServerConfig.armhangMoveSpeed);

		Vec3 beforeMove = player.position();
		player.move(MoverType.PLAYER, horizontalMovement);
		Vec3 afterMove = player.position();

		if (isFacingArmhangingDirection) {
			if (!isClimbableAtDirection(player, armhangingDirection)) {
				Vec3 safeEdgePosition = findSafeEdgePosition(player, beforeMove, afterMove, armhangingDirection);
				if (safeEdgePosition != null) {
					player.setPos(safeEdgePosition.x, safeEdgePosition.y, safeEdgePosition.z);
				} else {
					player.setPos(beforeMove.x, beforeMove.y, beforeMove.z);
				}
			}
		} else {
			if (!isClimbableAtDirection(player, armhangingDirection)) {
				boolean cornerRotated = handleOuterCornerRotation(player, wallData, afterMove, leftImpulse);
				if (!cornerRotated) {
					player.setPos(beforeMove.x, beforeMove.y, beforeMove.z);
				}
			} else {
				// 没有面向墙壁时无法移动
				player.setPos(beforeMove.x, beforeMove.y, beforeMove.z);
			}
		}
	}

	private static boolean handleOuterCornerRotation(LocalPlayer player, WallData wallData, Vec3 afterMove, float leftImpulse) {
		Direction currentDirection = Direction.from3DDataValue(wallData.getArmHangingDirection());
		Vec3 directionOffset = new Vec3(currentDirection.getStepX(), 0, currentDirection.getStepZ()).scale(CORNER_OFFSET);
		Vec3 cornerPosition = afterMove.add(directionOffset);

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

		Direction newDirection = leftImpulse > 0 ? currentDirection.getClockWise() : currentDirection.getCounterClockWise();

		// 为检测转角可攀爬性，临时将位置设为转角处以利用传感器
		player.setPos(cornerPosition.x, cornerPosition.y, cornerPosition.z);
		if (isClimbableAtDirection(player, newDirection)) {
			wallData.setArmHangingDirection(newDirection.get3DDataValue());
			player.sendPosition();
			// TODO: 发包给服务端同步 Direction
			// PacketDistributor.sendToServer(new ServerboundSyncArmHangingDirectionPacket(newDirection.get3DDataValue()));
			return true;
		}

		return false;
	}

	private static Vec3 findSafeEdgePosition(Player player, Vec3 startPos, Vec3 endPos, Direction direction) {
		Vec3 moveDirection = endPos.subtract(startPos);
		double totalDistance = moveDirection.length();

		if (totalDistance < EDGE_SEARCH_STEP) {
			return null;
		}

		Vec3 normalizedDirection = moveDirection.normalize();
		Vec3 lastSafePosition = null;

		for (int i = 1; i <= MAX_EDGE_SEARCH_ITERATIONS; i++) {
			double searchDistance = EDGE_SEARCH_STEP * i;
			if (searchDistance >= totalDistance) {
				break;
			}

			Vec3 searchPosition = startPos.add(normalizedDirection.scale(searchDistance));

			// 临时设置位置来复用 Sensor 检测逻辑
			Vec3 currentPos = player.position();
			player.setPos(searchPosition.x, searchPosition.y, searchPosition.z);
			boolean isClimbable = isClimbableAtDirection(player, direction);
			player.setPos(currentPos.x, currentPos.y, currentPos.z);

			if (isClimbable) {
				lastSafePosition = searchPosition;
			} else {
				break;
			}
		}

		return lastSafePosition;
	}
}