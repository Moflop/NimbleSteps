package mod.arcomit.nimblesteps.v2.core.statemachine.state;

import net.minecraft.world.entity.player.Player;

public interface IMovementStateTransition {
	IMovementState targetState();

	boolean canTrigger(Player player);
}
