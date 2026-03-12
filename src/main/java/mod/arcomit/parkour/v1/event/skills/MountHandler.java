//package mod.arcomit.nimblesteps.event.skills;
//
//import mod.arcomit.nimblesteps.NimbleStepsMod;
//import mod.arcomit.nimblesteps.ServerConfig;
//import mod.arcomit.nimblesteps.v2.context.MovementStateContext;
//import mod.arcomit.nimblesteps.network.serverbound.jump.ServerboundMountPacket;
//import mod.arcomit.nimblesteps.utils.TestUtils;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.core.Direction;
//import net.minecraft.world.entity.MoverType;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.phys.Vec3;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.api.distmarker.OnlyIn;
//import net.neoforged.bus.api.EventPriority;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.event.tick.PlayerTickEvent;
//import net.neoforged.neoforge.network.PacketDistributor;
//
///**
// * 支撑上墙处理器。
// *
// * @author Arcomit
// * @since 2026-01-18
// */
//@EventBusSubscriber(modid = NimbleStepsMod.MODID)
//public class MountHandler {
//	private static final double OBSTACLES_CHECK_DISTANCE = 0.5; // 检测距离，与垂挂检测距离保持一致
//
//	private static final double MOUNT_FORWARD_EXTRA_IMPULSE = 0.01; // 前进额外冲力
//	private static final double MOUNT_RISE_EXTRA_HEIGHT = 0.01; // 上升额外高度（防止刚好卡住上不去）
//	private static final int MOUNT_DURATION_TICKS = 12; // 持续时间（以刻为单位）
//	private static final double MOUNTING_RAPID_RISE_TICK_MULTIPLIER = 0.4; // 快速上升刻占整个持续时间的比例（40%）
//	private static final double MOUNTING_RAPID_RISE_HEIGHT_MULTIPLIER = 0.6; // 快速上升高度占整个上升高度的比例（60%）
//
//	@OnlyIn(Dist.CLIENT)
//	@SubscribeEvent(priority = EventPriority.HIGH)
//	public static void tryMount(PlayerTickEvent.Post event) {
//		if (!(event.getEntity() instanceof LocalPlayer localPlayer)) {
//			return;
//		}
//		MovementStateContext state = MovementStateContext.getNimbleState(localPlayer);
//
//		boolean jumpNotPressed = !localPlayer.input.up;
//		if (jumpNotPressed) {
//			return;
//		}
//
//		if (!canStartMount(localPlayer, state)) {
//			return;
//		}
//		startMount(localPlayer, state);
//		PacketDistributor.sendToServer(new ServerboundMountPacket());
//	}
//
//	@SubscribeEvent
//	public static void handlerMount(PlayerTickEvent.Post event) {
//		if (!(event.getEntity() instanceof Player player)) {
//			return;
//		}
//		MovementStateContext state = MovementStateContext.getNimbleState(player);
//		if (!state.isMounting()) {
//			return;
//		}
//
//		// 清除重力并施加额外的前进冲力
//		Vec3 look = new Vec3(player.getLookAngle().x, 0 , player.getLookAngle().z)
//			.normalize().scale(MOUNT_FORWARD_EXTRA_IMPULSE);
//		Vec3 deltaMove = new Vec3(player.getDeltaMovement().x, 0 , player.getDeltaMovement().z)
//			.add(look);
//		player.setDeltaMovement(deltaMove);
//		// 如果在快速上升阶段，给予更高的上升速度
//		double mountDurationTick = MOUNT_DURATION_TICKS * (state.getObstaclesHeight() / 2);
//		int rapidRiseTicks = (int)(mountDurationTick * MOUNTING_RAPID_RISE_TICK_MULTIPLIER);
//		int slowRiseTicks = (int) (mountDurationTick - rapidRiseTicks);
//		if (state.getMountDuration() > slowRiseTicks) {
//			player.move(MoverType.PLAYER, new Vec3(0, (state.getObstaclesHeight() + MOUNT_RISE_EXTRA_HEIGHT) * MOUNTING_RAPID_RISE_HEIGHT_MULTIPLIER / rapidRiseTicks, 0));
//		} else {
//			player.move(MoverType.PLAYER, new Vec3(0, ((state.getObstaclesHeight() + MOUNT_RISE_EXTRA_HEIGHT) * (1 - MOUNTING_RAPID_RISE_HEIGHT_MULTIPLIER)) / slowRiseTicks, 0));
//		}
//
//		state.setMountDuration(state.getMountDuration() - 1);
//	}
//
//	public static void startMount(Player player, MovementStateContext state) {
//		Direction armhangDir = Direction.from3DDataValue(state.getArmHangingDirection());
//		Vec3 armhangVec = new Vec3(armhangDir.getStepX(), armhangDir.getStepY(), armhangDir.getStepZ());
//		Vec3 mountVec = armhangVec.scale(OBSTACLES_CHECK_DISTANCE);
//		double obstaclesHeight = TestUtils.getObstaclesHeight(player, mountVec);
//		if (obstaclesHeight != 0 && TestUtils.hasEnoughSpaceAbove(player, mountVec, obstaclesHeight)) {
//			double mountDurationTick = MOUNT_DURATION_TICKS * (state.getObstaclesHeight() / 2);
//			state.setMountDuration((int) mountDurationTick);
//			state.setObstaclesHeight(obstaclesHeight);
//			ArmhangHandler.endArmhang(state);
//		}
//	}
//
//	private static boolean isFacingArmHangingDirection(Player player, MovementStateContext state) {
//		Direction armhangDir = Direction.from3DDataValue(state.getArmHangingDirection());
//		Direction facing = player.getDirection();
//		return armhangDir == facing;
//	}
//
//	public static boolean canStartMount(Player player, MovementStateContext state) {
//		return ServerConfig.enableMount
//			&& state.isArmHanging()
//			&& isFacingArmHangingDirection(player, state);
//	}
//}
