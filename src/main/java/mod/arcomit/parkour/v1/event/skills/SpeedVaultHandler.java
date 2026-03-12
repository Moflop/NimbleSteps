//package mod.arcomit.nimblesteps.event.skills;
//
//import com.mojang.blaze3d.platform.InputConstants;
//import mod.arcomit.nimblesteps.v2.context.MovementStateContext;
//import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
//import mod.arcomit.nimblesteps.utils.TestUtils;
//import net.minecraft.client.Minecraft;
//import net.minecraft.world.entity.MoverType;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.phys.Vec3;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.api.distmarker.OnlyIn;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.neoforge.client.event.InputEvent;
//import net.neoforged.neoforge.event.tick.PlayerTickEvent;
//
///**
// * TODO：描述
// *
// * @author Arcomit
// * @since 2026-01-22
// */
////@EventBusSubscriber(modid = NimbleStepsMod.MODID)
//public class SpeedVaultHandler {
//	private static final double OBSTACLES_CHECK_DISTANCE = 0.5; // 检测距离，与垂挂检测距离保持一致
//
//	private static final double MOUNT_FORWARD_EXTRA_IMPULSE = 0.01; // 前进额外冲力
//	private static final double MOUNT_RISE_EXTRA_HEIGHT = 0.01; // 上升额外高度（防止刚好卡住上不去）
//	private static final int MOUNT_DURATION_TICKS = 5; // 持续时间（以刻为单位）
//	private static final double MOUNTING_RAPID_RISE_TICK_MULTIPLIER = 0.4; // 快速上升刻占整个持续时间的比例（40%）
//	private static final double MOUNTING_RAPID_RISE_HEIGHT_MULTIPLIER = 0.6; // 快速上升高度占整个上升高度的比例（60%）
//
//
//	@OnlyIn(Dist.CLIENT)
//	@SubscribeEvent
//	public static void onKeyPress(InputEvent.Key event) {
//		trySpeedVaultOnInput(event.getAction(), event.getKey());
//	}
//
//	@OnlyIn(Dist.CLIENT)
//	@SubscribeEvent
//	public static void onMousePress(InputEvent.MouseButton.Post event) {
//		trySpeedVaultOnInput(event.getAction(), event.getButton());
//	}
//
//	@OnlyIn(Dist.CLIENT)
//	private static void trySpeedVaultOnInput(int action, int inputKey) {
//		Minecraft mc = Minecraft.getInstance();
//		Player player = mc.player;
//
//		if (player == null || mc.screen != null) {
//			return;
//		}
//		if (action != InputConstants.PRESS) {
//			return;
//		}
//		if (inputKey != mc.options.keyJump.getKey().getValue()) {
//			return;
//		}
//
//		MovementStateContext state = MovementStateContext.getNimbleState(player);
//		if (!canStartSpeedVault(player, state)) {
//			return;
//		}
//
//		Vec3 look = player.getLookAngle().normalize().scale(0.5);
//		Vec3 checkVec = new Vec3(look.x, 0, look.z);
//		double obstaclesHeight = TestUtils.getObstaclesHeight(player, checkVec);
//		if (obstaclesHeight < 0.6 || obstaclesHeight > 1.5) {
//			return;
//		}
//
//		if (TestUtils.hasEnoughSpaceAbove(player, checkVec, obstaclesHeight)) {
//			state.setMountDuration(5);
//			state.setObstaclesHeight(obstaclesHeight);
//			ArmhangHandler.endArmhang(state);
//
//		}
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
//		int rapidRiseTicks = (int)(MOUNT_DURATION_TICKS * MOUNTING_RAPID_RISE_TICK_MULTIPLIER);
//		int slowRiseTicks = MOUNT_DURATION_TICKS - rapidRiseTicks;
//		if (state.getMountDuration() > slowRiseTicks) {
//			player.move(MoverType.PLAYER, new Vec3(0, (state.getObstaclesHeight() + MOUNT_RISE_EXTRA_HEIGHT) * MOUNTING_RAPID_RISE_HEIGHT_MULTIPLIER / rapidRiseTicks, 0));
//		} else {
//			player.move(MoverType.PLAYER, new Vec3(0, ((state.getObstaclesHeight() + MOUNT_RISE_EXTRA_HEIGHT) * (1 - MOUNTING_RAPID_RISE_HEIGHT_MULTIPLIER)) / slowRiseTicks, 0));
//		}
//
//		state.setMountDuration(state.getMountDuration() - 1);
//	}
//
//	public static boolean canStartSpeedVault(Player player, MovementStateContext state) {
//		return player.onGround()
//			&& PlayerStateUtils.isAbleToAction(player);
//	}
//}
