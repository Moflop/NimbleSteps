package mod.arcomit.parkour.v2.content.client.handler;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.InputData;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-11
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class InputHandler {

	@SubscribeEvent
	public static void recordInput(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer player)) return;

		InputData inputData = MovementStateContext.get(player).getInputData();
		boolean jumpActiveData = inputData.isJumpKeyActive();
		boolean isJumpKeyActive = player.input.jumping;
		if (jumpActiveData != isJumpKeyActive) {
			inputData.setJumpKeyActive(player.input.jumping);
			// 发包给服务端
		}
	}
}
