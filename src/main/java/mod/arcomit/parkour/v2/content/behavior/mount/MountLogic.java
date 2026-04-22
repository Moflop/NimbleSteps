package mod.arcomit.parkour.v2.content.behavior.mount;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.TestUtils;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.WallData;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 翻越与上墙逻辑。
 *
 * @author Arcomit
 */
public class MountLogic {
	private static final double MOUNT_FORWARD_EXTRA_IMPULSE = 0.1; // 额外前进冲力
	private static final int MOUNT_DURATION_TICKS = 10; // 基础翻越时间
	private static final double MOUNT_RISE_EXTRA_HEIGHT = 0.2;
	private static final double MOUNTING_RAPID_RISE_TICK_MULTIPLIER = 0.4;
	private static final double MOUNTING_RAPID_RISE_HEIGHT_MULTIPLIER = 0.8;
	private static final double OBSTACLES_CHECK_DISTANCE = 1.0;

	/**
	 * 判断垂挂状态下是否能触发上墙
	 */
	public static boolean canStartMountFromArmhang(Player player) {
		if (!ServerConfig.enableMount) return false;

		ParkourContext context = ParkourContext.get(player);
		WallData wallData = context.wallData();
		Direction armhangDir = Direction.from3DDataValue(wallData.getArmHangingDirection());

		if (armhangDir != player.getDirection()) {
			return false; // 必须面向墙壁
		}

		Vec3 armhangVec = new Vec3(armhangDir.getStepX(), armhangDir.getStepY(), armhangDir.getStepZ());
		Vec3 mountVec = armhangVec.scale(OBSTACLES_CHECK_DISTANCE);

		// 假设 TestUtils 依然存在于 v2，或者你已经迁移
		double obstaclesHeight = TestUtils.getObstaclesHeight(player, mountVec);
		return obstaclesHeight != 0 && TestUtils.hasEnoughSpaceAbove(player, mountVec, obstaclesHeight);
	}

	/**
	 * 初始化 Mount 参数
	 */
	public static void startMount(Player player, ParkourContext context) {
		WallData wallData = context.wallData();
		Direction armhangDir = Direction.from3DDataValue(wallData.getArmHangingDirection());
		Vec3 mountVec = new Vec3(armhangDir.getStepX(), armhangDir.getStepY(), armhangDir.getStepZ()).scale(OBSTACLES_CHECK_DISTANCE);

		double obstaclesHeight = TestUtils.getObstaclesHeight(player, mountVec);
		double mountDurationTick = MOUNT_DURATION_TICKS * (obstaclesHeight / 2.0);

		wallData.setMountDuration((int) Math.max(5, mountDurationTick));
		wallData.setObstaclesHeight(obstaclesHeight);
	}

	/**
	 * 应用 Mount 期间的物理位移
	 */
	public static void applyMountMovement(Player player, ParkourContext context) {
		WallData wallData = context.wallData();

		// 清除重力并施加额外的前进冲力
		Vec3 lookAngle = player.getLookAngle();
		Vec3 look = new Vec3(lookAngle.x, 0 , lookAngle.z).normalize().scale(MOUNT_FORWARD_EXTRA_IMPULSE);
		Vec3 deltaMove = new Vec3(player.getDeltaMovement().x, 0 , player.getDeltaMovement().z).add(look);
		player.setDeltaMovement(deltaMove);

		// 如果在快速上升阶段，给予更高的上升速度
		double totalDuration = MOUNT_DURATION_TICKS * (wallData.getObstaclesHeight() / 2.0);
		int rapidRiseTicks = Math.max(1, (int)(totalDuration * MOUNTING_RAPID_RISE_TICK_MULTIPLIER));
		int slowRiseTicks = Math.max(1, (int)(totalDuration - rapidRiseTicks));

		double currentDuration = wallData.getMountDuration();
		double totalRiseHeight = wallData.getObstaclesHeight() + MOUNT_RISE_EXTRA_HEIGHT;

		if (currentDuration > slowRiseTicks) {
			player.move(MoverType.PLAYER, new Vec3(0, (totalRiseHeight * MOUNTING_RAPID_RISE_HEIGHT_MULTIPLIER) / rapidRiseTicks, 0));
		} else {
			player.move(MoverType.PLAYER, new Vec3(0, (totalRiseHeight * (1 - MOUNTING_RAPID_RISE_HEIGHT_MULTIPLIER)) / slowRiseTicks, 0));
		}

		wallData.setMountDuration(wallData.getMountDuration() - 1);
	}
}