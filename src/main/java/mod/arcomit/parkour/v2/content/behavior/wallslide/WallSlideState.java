package mod.arcomit.parkour.v2.content.behavior.wallslide;

import mod.arcomit.parkour.v2.content.behavior.base.DefaultState;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v2.content.behavior.slide.WallSlideLogic;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 滑墙状态
 *
 * @author Arcomit
 */
public class WallSlideState implements IParkourState {
	public static final WallSlideState INSTANCE = new WallSlideState();

	@Override
	public Iterable<IParkourStateTransition> transitions() {
		return List.of(
			new IParkourStateTransition() {
				@Override
				public IParkourState targetState() {
					return DefaultState.INSTANCE;
				}

				@Override
				public boolean canTrigger(Player player) {
					MovementStateContext context = MovementStateContext.get(player);
					// 如果不再被标记为滑墙（或者不满足滑墙条件），则退回默认状态
					return !context.getWallData().isWallSliding()
						|| !WallSlideLogic.isValid(player, context);
				}
			}
		);
	}

	@Override
	public void onEnter(Player player, IParkourState previousState) {
	}

	@Override
	public void onExit(Player player, IParkourStateTransition triggeredTransition) {
		MovementStateContext context = MovementStateContext.get(player);
		WallSlideLogic.endWallSliding(context.getWallData());
	}

	@Override
	public void onTick(Player player) {
		MovementStateContext context = MovementStateContext.get(player);
		WallSlideLogic.useWallSlideMovement(player, context);
	}
}