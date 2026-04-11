package mod.arcomit.parkour;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.animation.ParkourModifierRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class ParkourClientSetup {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ParkourModifierRegistry.registerAll();

		});
	}
}