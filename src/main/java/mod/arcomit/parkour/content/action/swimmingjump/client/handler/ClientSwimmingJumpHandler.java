package mod.arcomit.parkour.content.action.swimmingjump.client.handler;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.content.action.swimmingjump.network.UseSwimmingJumpC2SPayload;
import mod.arcomit.parkour.content.action.swimmingjump.SwimmingJumpAction;
import mod.arcomit.parkour.core.context.ParkourContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-15
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class ClientSwimmingJumpHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void trySwimmingJump(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if (!(player instanceof LocalPlayer localPlayer)) {
			return;
		}

		SwimmingJumpAction.execute(localPlayer);
		localPlayer.sendPosition();
		PacketDistributor.sendToServer(new UseSwimmingJumpC2SPayload());
	}
}
