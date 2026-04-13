package mod.arcomit.parkour;

import mod.arcomit.parkour.v2.core.animation.camera.CameraAnimationRegistry;
import mod.arcomit.parkour.v2.core.animation.player.PlayerAnimModifierRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class ParkourClientSetup {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			PlayerAnimModifierRegistry.registerAll();
		});
	}

	@SubscribeEvent
	public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
		// 注册我们的动画加载器
		event.registerReloadListener(CameraAnimationRegistry.INSTANCE);
	}
}