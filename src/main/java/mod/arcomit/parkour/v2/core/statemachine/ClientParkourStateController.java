package mod.arcomit.parkour.v2.core.statemachine;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.client.event.InputJustPressedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 跑酷状态机的客户端控制器，监听客户端输入事件并将其传递给状态机
 *
 * @author Arcomit
 * @since 2026-03-14
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ParkourMod.MODID)
public class ClientParkourStateController {

	@SubscribeEvent
	public static void onKeyJustPressed(InputJustPressedEvent event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;
		ParkourStateMachine.tryInputTransition(player, event.getKeyMapping());
	}
}
