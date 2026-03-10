package mod.arcomit.nimblesteps.v2.core.statemachine;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-09
 */
@EventBusSubscriber
public class StateMachineManager {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		MovementStateMachine stateMachine = MovementStateMachine.get(player);

		if (stateMachine != null) {
			stateMachine.tick();
		}
	}
}
