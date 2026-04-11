package mod.arcomit.parkour.v2.content.behavior.backstep.common;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * 后撤步无敌帧
 *
 * @author Arcomit
 * @since 2026-04-11
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class BackstepHandler {

	@SubscribeEvent
	public static void onPlayerIncomingDamage(LivingIncomingDamageEvent event) {
		if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {

			StateData stateData = ParkourContext.get(player).stateData();

			if (stateData.getState() == PkParkourStates.BACKSTEP.get()) {
				event.setCanceled(true);
			}
		}
	}
}
