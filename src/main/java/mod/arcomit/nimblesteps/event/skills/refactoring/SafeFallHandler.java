package mod.arcomit.nimblesteps.event.skills.refactoring;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

/**
 * 比牢大更能抗摔处理器。
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class SafeFallHandler {
	private static final float VANILLA_SAFE_FALL_DISTANCE = 3.0f;

	@SubscribeEvent
	public static void adjustFallDistanceForSafeFall(LivingFallEvent event) {
		if (!ServerConfig.enableSafeFallHeightIncrease) {
			return;
		}

		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		float distance = event.getDistance();
		float adjustment = (float) ServerConfig.safeFallHeight - VANILLA_SAFE_FALL_DISTANCE;
		event.setDistance(distance - adjustment);
	}
}
