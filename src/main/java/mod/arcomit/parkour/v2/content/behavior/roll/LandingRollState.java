//package mod.arcomit.parkour.v2.content.behavior.roll;
//
//import mod.arcomit.parkour.v2.content.init.PkParkourStates;
//import mod.arcomit.parkour.v2.core.context.GroundData;
//import mod.arcomit.parkour.v2.core.context.ParkourContext;
//import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
//import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
//import net.minecraft.world.entity.Pose;
//import net.minecraft.world.entity.player.Player;
//
//import java.util.List;
//
///**
// * 落地翻滚状态
// * * @author Arcomit
// */
//public class LandingRollState implements IParkourState {
//	public static final LandingRollState INSTANCE = new LandingRollState();
//
//	@Override
//	public List<IParkourStateTransition> getTransitions() {
//		return List.of(
//			new IParkourStateTransition() {
//				@Override
//				public IParkourState getTargetState() {
//					return PkParkourStates.DEFAULT.get();
//				}
//
//				@Override
//				public boolean shouldTransitionOnTick(Player player) {
//					GroundData groundData = ParkourContext.get(player).groundData();
//					return groundData.getLandingRollDuration() <= 0
//						|| !LandingRollLogic.isValid(player);
//				}
//			}
//		);
//	}
//
//	@Override
//	public void onEnter(Player player, IParkourState previousState) {
//		player.setForcedPose(Pose.SWIMMING);
//		// todo:播放摄像机动画
//	}
//
//	@Override
//	public void onExit(Player player, IParkourStateTransition triggeredTransition) {
//		GroundData groundData = ParkourContext.get(player).groundData();
//		groundData.setLandingRollDuration(0);
//		player.setForcedPose(null);
//	}
//
//	@Override
//	public void onTick(Player player) {
//		GroundData groundData = ParkourContext.get(player).groundData();
//		int currentDuration = groundData.getLandingRollDuration();
//		if (currentDuration > 0) {
//			groundData.setLandingRollDuration(currentDuration - 1);
//		}
//	}
//
//}