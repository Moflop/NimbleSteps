package mod.arcomit.parkour.v2.core.statemachine;

import lombok.Getter;
import mod.arcomit.parkour.v1.init.NsAttachmentTypes;
import mod.arcomit.parkour.v2.content.behavior.base.DefaultState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：状态机只管理状态，不持久化存储数据
 *
 * @author Arcomit
 * @since 2026-03-09
 */
public class ParkourStateMachine {
	@Getter
	private IParkourState currentState = DefaultState.INSTANCE;

	public void tick(Player player) {
		currentState.onTick(player);
		tryTransition(player);
	}

	private void tryTransition(Player player) {
		for (IParkourStateTransition transition : currentState.transitions()) {
			if (transition.canTrigger(player)) {
				transitionTo(player, transition.targetState(), transition);
				return;
			}
		}
	}

	private void transitionTo(Player player, IParkourState newState, IParkourStateTransition transition) {
		if (newState != null) {
			currentState.onExit(player, transition);

			IParkourState previousState = currentState;
			currentState = newState;

			currentState.onEnter(player, previousState);
		}
	}

	public void forceTransitionTo(Player player, IParkourState newState) {
		// 直接传入 null 作为 transition，状态的 onExit 中可据此判断是否是强制打断
		transitionTo(player, newState, null);
	}

	public boolean isDefaultState() {
		return  currentState == DefaultState.INSTANCE;
	}

	public static ParkourStateMachine get(Player player) {
		return player.getData(NsAttachmentTypes.MOVEMENT_STATE_MACHINE);
	}
}
