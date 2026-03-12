package mod.arcomit.parkour.v2.core.statemachine.state;

import net.minecraft.world.entity.player.Player;

public interface IParkourStateTransition {
	IParkourState targetState();

	boolean canTrigger(Player player);
}
