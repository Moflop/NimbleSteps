package mod.arcomit.parkour.v2.content.behavior.landingroll.client;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.v1.network.serverbound.roll.ServerboundSetLandingRollWindowPacket;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.client.input.ParkourKeyBindings;
import mod.arcomit.parkour.v2.content.client.input.ParkourKeyMapping;
import mod.arcomit.parkour.v2.content.client.event.InputJustPressedEvent;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class ClientLandingRollHandler {

	@SubscribeEvent
	public static void trySetLandingRollWindowOnInput(InputJustPressedEvent event) {
		ParkourKeyMapping key = event.getKeyMapping();
		if (key != ParkourKeyBindings.SLIDE_KEY) {
			return;
		}

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || !PlayerStateUtils.fallWillTakeDamage(player)) {
			return;
		}

		GroundData groundData = ParkourContext.get(player).groundData();
		if (groundData.getLandingRollWindow() > 0) {
			return;
		}

		groundData.setLandingRollWindow(ParkourConfig.landingRollWindow);
		PacketDistributor.sendToServer(new ServerboundSetLandingRollWindowPacket());
	}
}
