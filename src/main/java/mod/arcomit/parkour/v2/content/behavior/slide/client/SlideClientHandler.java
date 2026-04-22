package mod.arcomit.parkour.v2.content.behavior.slide.client;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

/**
 * 滑铲状态禁止跳跃
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class SlideClientHandler {

	@SubscribeEvent
	public static void disableJumpWhileSliding(MovementInputUpdateEvent event) {
		Player player = event.getEntity();
		StateData stateData = ParkourContext.get(player).stateData();

		if (stateData.getState() == PkParkourStates.SLIDE.get()){
			event.getInput().jumping = false;
		}
	}

}
