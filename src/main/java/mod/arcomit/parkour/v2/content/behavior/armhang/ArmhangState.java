//package mod.arcomit.parkour.v2.content.behavior.armhang;
//
//import mod.arcomit.parkour.v2.content.behavior.mount.MountLogic;
//import mod.arcomit.parkour.v2.content.init.ParkourStates;
//import mod.arcomit.parkour.v2.core.context.ParkourContext;
//import mod.arcomit.parkour.v2.core.context.WallData;
//import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
//import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.core.Direction;
//import net.minecraft.world.entity.Pose;
//import net.minecraft.world.entity.player.Player;
//
///**
// * 垂挂状态。
// *
// * @author Arcomit
// */
//// TODO:重构
//public class ArmhangState extends AbstractParkourState {
//
//	public ArmhangState() {
//		registerTransitions(
//			// 优先判断：如果按下跳跃键，且满足上墙条件，则转换到 MOUNT
//			IParkourStateTransition.onTick(
//				ParkourStates.MOUNT::get,
//				(player, context) -> {
//					// 判断本地或同步的输入
//					boolean isJumping = player instanceof LocalPlayer lp ? lp.input.jumping : context.inputData().isJumpKeyActive();
//					return isJumping && MountLogic.canStartMountFromArmhang(player);
//				}
//			),
//			IParkourStateTransition.onTick(
//				ParkourStates.DEFAULT::get,
//				(player, context) -> player.isShiftKeyDown()
//			)
//		);
//	}
//
//	@Override
//	public void onEnter(Player player, ParkourContext context) {
//		WallData wallData = context.wallData();
//		wallData.setArmHanging(true);
//		wallData.setArmHangingDir(player.getDirection().get3DDataValue());
//
//		if (player instanceof LocalPlayer localPlayer) {
//			localPlayer.sendPosition();
//		}
//	}
//
//	@Override
//	public void onTick(Player player, ParkourContext context) {
//		super.onTick(player, context);
//		// 施加物理运动影响
//		ArmhangLogic.applyArmhangMovement(player, context);
//	}
//
//	@Override
//	public void onExit(Player player, ParkourContext context) {
//		WallData wallData = context.wallData();
//		wallData.setArmHanging(false);
//		wallData.resetArmHangingDirection();
//	}
//
//	@Override
//	public Pose getLinkedPose() {
//		return Pose.STANDING; // 如有自定义POSE可在此修改
//	}
//
//	@Override
//	public boolean canEnter(Player player, ParkourContext context) {
//		return ArmhangLogic.canStartArmhang(player)
//			&& ArmhangLogic.isClimbableAtDirection(player, player.getDirection());
//	}
//
//	@Override
//	public boolean isValid(Player player, ParkourContext context) {
//		WallData wallData = context.wallData();
//		Direction direction = Direction.from3DDataValue(wallData.getArmHangingDir());
//
//		return ArmhangLogic.isClimbableAtDirection(player, direction);
//	}
//}