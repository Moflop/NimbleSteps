package mod.arcomit.parkour.v2.content.behavior.roll.client;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.network.serverbound.roll.ServerboundSetLandingRollWindowPacket;
import mod.arcomit.parkour.v2.content.client.NsKeyBindings;
import mod.arcomit.parkour.v2.content.client.NsKeyMapping;
import mod.arcomit.parkour.v2.content.client.event.InputJustPressedEvent;
import mod.arcomit.parkour.v2.core.context.GroundMovementData;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v2.content.behavior.roll.LandingRollLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class LandingRollClientHandler {

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void trySetLandingRollWindowOnInput(InputJustPressedEvent event) {
		NsKeyMapping key = event.getKeyMapping();
		if (key != NsKeyBindings.SLIDE_KEY) {
			return;
		}

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
		if (LandingRollLogic.cannotSetLandingRollWindow(player, groundData)) {
			return;
		}

		groundData.setLandingRollWindow(ServerConfig.landingRollWindow);
		PacketDistributor.sendToServer(new ServerboundSetLandingRollWindowPacket());
	}
}
