package mod.arcomit.parkour.v2.content.handler;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.SwimData;
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
@EventBusSubscriber(modid = ParkourMod.MODID)
public class TimerHandler {

	@SubscribeEvent
	public static void tickCooldownTimers(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		ParkourContext context = ParkourContext.get(player);
		GroundData groundData = context.groundData();
		SwimData swimData = context.swimData();

		// 滑铲冷却
		int slideCooldown = groundData.getSlideCooldown();
		if (slideCooldown > 0) groundData.setSlideCooldown(slideCooldown - 1);

		// 落地翻滚窗口倒计时
		int rollWindow = groundData.getLandingRollWindow();
		if (rollWindow > 0) groundData.setLandingRollWindow(rollWindow - 1);

		// 水中推进冷却
		int swimmingBoostCooldown = swimData.getSwimmingBoostCooldown();
		if (swimmingBoostCooldown > 0) swimData.setSwimmingBoostCooldown(swimmingBoostCooldown - 1);
	}
}
