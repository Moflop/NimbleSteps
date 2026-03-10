package mod.arcomit.nimblesteps.v2.content.state;

import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
import mod.arcomit.nimblesteps.v2.core.statemachine.state.IMovementState;
import mod.arcomit.nimblesteps.v2.core.statemachine.state.IMovementStateTransition;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-09
 */
public class DefaultState implements IMovementState {

	public static final DefaultState INSTANCE = new DefaultState();

	@Override
	public Iterable<IMovementStateTransition> transitions() {
		return List.of(
			new IMovementStateTransition() {
				@Override
				public IMovementState targetState() {
					return CrawlState.INSTANCE;
				}

				@Override
				public boolean canTrigger(Player player) {
					MovementStateContext context = MovementStateContext.get(player);
					return context.getGroundData().isCrawling();
				}
			},
			new IMovementStateTransition() {
				@Override
				public IMovementState targetState() {
					return SlideState.INSTANCE;
				}

				@Override
				public boolean canTrigger(Player player) {
					MovementStateContext context = MovementStateContext.get(player);
					return context.getGroundData().getSlideDuration() > 0;
				}
			}
		);
	}

	@Override
	public void onEnter(Player player, IMovementState previousState) {}

	@Override
	public void onExit(Player player, IMovementStateTransition triggeredTransition) {}

	@Override
	public void onTick(Player player) {}
}
