//package mod.arcomit.parkour.v2.content.behavior.wallslide;
//
//import mod.arcomit.parkour.v2.content.init.PkParkourStates;
//import mod.arcomit.parkour.v2.core.context.ParkourContext;
//import mod.arcomit.parkour.v2.content.behavior.slide.WallSlideLogic;
//import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
//import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
//import net.minecraft.world.entity.player.Player;
//
//import java.util.List;
//
///**
// * 滑墙状态
// *
// * @author Arcomit
// */
//public class WallSlideState implements IParkourState {
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
//					ParkourContext context = ParkourContext.get(player);
//					// 如果不再被标记为滑墙（或者不满足滑墙条件），则退回默认状态
//					return !context.wallData().isWallSliding()
//						|| !WallSlideLogic.isValid(player, context);
//				}
//			}
//		);
//	}
//
//	@Override
//	public void onEnter(Player player, IParkourState previousState) {
//	}
//
//	@Override
//	public void onExit(Player player, IParkourStateTransition triggeredTransition) {
//		ParkourContext context = ParkourContext.get(player);
//		WallSlideLogic.endWallSliding(context.wallData());
//	}
//
//	@Override
//	public void onTick(Player player) {
//		ParkourContext context = ParkourContext.get(player);
//		WallSlideLogic.useWallSlideMovement(player, context);
//	}
//}