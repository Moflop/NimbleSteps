package mod.arcomit.nimblesteps.v2.core.statemachine.state;

import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-09
 */
public class ForcedTransition implements IMovementStateTransition {

	private final IMovementState targetState;

	public ForcedTransition(IMovementState targetState) {
		this.targetState = targetState;
	}

	@Override
	public IMovementState targetState() {
		return this.targetState;
	}

	@Override
	public boolean canTrigger(Player player) {
		return false;
	}
}