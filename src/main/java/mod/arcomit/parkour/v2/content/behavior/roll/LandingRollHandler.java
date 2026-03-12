package mod.arcomit.parkour.v2.content.behavior.roll;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.core.context.GroundMovementData;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class LandingRollHandler {

	@SubscribeEvent
	public static void tryLandingRollOnFall(LivingFallEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!PlayerStateUtils.fallWillTakeDamage(player)) return;

		GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
		if (groundData.getLandingRollWindow() <= 0) return;

		// 条件满足，调用逻辑层
		LandingRollLogic.performLandingRoll(player, groundData, event);
	}
}