package mod.arcomit.parkour.v2.content.behavior.wallclimb.common;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.WallData;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-16
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class WallClimbHandler {
	@SubscribeEvent
	public static void resetWallClimbState(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		WallData wallData = ParkourContext.get(player).wallData();
		if (player.onGround() && wallData.isWallClimbed()) {
			wallData.setWallClimbed(false);
		}
	}
}