package mod.arcomit.parkour.content.action.swimmingboost.client.handler;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.content.action.swimmingboost.network.ServerboundUseSwimmingBoostPacket;
import mod.arcomit.parkour.content.client.input.ParkourKeyBindings;
import mod.arcomit.parkour.content.client.input.ParkourKeyMapping;
import mod.arcomit.parkour.content.client.event.InputJustPressedEvent;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.context.SwimData;
import mod.arcomit.parkour.content.action.swimmingboost.SwimmingBoostLogic;
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
public class ClientSwimmingBoostHandler {

	@SubscribeEvent
	public static void trySwimmingBoostOnInput(InputJustPressedEvent event) {
		ParkourKeyMapping key = event.getKeyMapping();
		if (key != ParkourKeyBindings.SLIDE_KEY) {
			return;
		}

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		SwimData swimData = ParkourContext.get(player).swimData();

		// 调用逻辑层的判断条件
		if (!SwimmingBoostLogic.canSwimmingBoost(player, swimData)) {
			return;
		}

		// 1. 本地客户端执行推进逻辑
		SwimmingBoostLogic.useSwimmingBoost(player, swimData);

		// 2. 告知服务端执行相同的逻辑
		PacketDistributor.sendToServer(new ServerboundUseSwimmingBoostPacket());
	}
}
