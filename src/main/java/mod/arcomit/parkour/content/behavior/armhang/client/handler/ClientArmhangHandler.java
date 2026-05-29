package mod.arcomit.parkour.content.behavior.armhang.client.handler;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.core.context.InputData;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.context.StateData;
import mod.arcomit.parkour.core.statemachine.state.IParkourState;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-21
 */
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class ClientArmhangHandler {

	@SubscribeEvent
	public static void disableMoveWhileArmhanging(MovementInputUpdateEvent event) {
		Player player = event.getEntity();
		StateData stateData = ParkourContext.get(player).stateData();
		IParkourState currentState = stateData.getState();
		if (currentState != ParkourStates.ARMHANG.get()) {
			return;
		}

		// 垂挂状态禁用移动。
		Input input = event.getInput();
		input.forwardImpulse = 0;
		InputData inputData = ParkourContext.get(player).inputData();
		inputData.setLeftImpulse(input.leftImpulse);
		input.leftImpulse = 0;
	}
}
