package mod.arcomit.nimblesteps.v2.content.handler;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.v2.content.context.GroundMovementData;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class CooldownTimerHandler {

	@SubscribeEvent
	public static void tickCooldownTimers(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		MovementStateContext context = MovementStateContext.get(player);
		GroundMovementData groundData = context.getGroundData();

		// 滑铲冷却
		int slideCooldown = groundData.getSlideCooldown();
		if (slideCooldown > 0) {
			groundData.setSlideCooldown(slideCooldown - 1);
		}
	}
}
