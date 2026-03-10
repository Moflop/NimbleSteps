package mod.arcomit.nimblesteps.v2.core.statemachine.state;

import net.minecraft.world.entity.player.Player;

public interface IMovementState {
	Iterable<IMovementStateTransition> transitions();

	void onEnter(Player player, IMovementState previousState);

	void onExit(Player player, IMovementStateTransition triggeredTransition);

	void onTick(Player player);
}
