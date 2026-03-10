package mod.arcomit.nimblesteps.v2.core.statemachine;

import lombok.Getter;
import mod.arcomit.nimblesteps.v1.init.NsAttachmentTypes;
import mod.arcomit.nimblesteps.v2.content.state.DefaultState;
import mod.arcomit.nimblesteps.v2.core.statemachine.state.ForcedTransition;
import mod.arcomit.nimblesteps.v2.core.statemachine.state.IMovementState;
import mod.arcomit.nimblesteps.v2.core.statemachine.state.IMovementStateTransition;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：状态机只管理状态，不持久化存储数据
 *
 * @author Arcomit
 * @since 2026-03-09
 */
public class MovementStateMachine {
	private Player player;
	@Getter
	private IMovementState currentState = DefaultState.INSTANCE;

	public MovementStateMachine(Player player) {
		this.player = player;
	}

	public void tick() {
		tryTransition();
		currentState.onTick(player);
	}

	private void tryTransition() {
		for (IMovementStateTransition transition : currentState.transitions()) {
			if (transition.canTrigger(player)) {
				transitionTo(transition);
				return;
			}
		}
	}

	private void transitionTo(IMovementStateTransition transition) {
		IMovementState newState = transition.targetState();
		if (newState != null && newState != currentState) {
			currentState.onExit(player, transition);
			IMovementState previousState = currentState;
			currentState = newState;
			currentState.onEnter(player, previousState);
		}
	}

	public void forceTransitionTo(IMovementState newState) {
		transitionTo(new ForcedTransition(newState));
	}

	public boolean isDefaultState() {
		return  currentState == DefaultState.INSTANCE;
	}

	public static MovementStateMachine get(Player player) {
		return player.getData(NsAttachmentTypes.MOVEMENT_STATE_MACHINE);
	}
}
