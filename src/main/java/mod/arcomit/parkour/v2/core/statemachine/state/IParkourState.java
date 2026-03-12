package mod.arcomit.parkour.v2.core.statemachine.state;

import net.minecraft.world.entity.player.Player;

public interface IParkourState {

	String getId();

	Iterable<IParkourStateTransition> transitions();

	default void onEnter(Player player, IParkourState previousState) {}

	default void onExit(Player player, IParkourStateTransition triggeredTransition) {}

	default void onTick(Player player) {}
}
