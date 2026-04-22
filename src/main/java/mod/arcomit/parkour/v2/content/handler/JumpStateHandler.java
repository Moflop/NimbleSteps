package mod.arcomit.parkour.v2.content.handler;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.JumpData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-15
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class JumpStateHandler {
	@SubscribeEvent
	public static void onJump(LivingEvent.LivingJumpEvent event) {
		if (event.getEntity() instanceof LocalPlayer player) {
			JumpData jumpData = ParkourContext.get(player).jumpData();
			jumpData.setTicksSinceLastJump(0);
			//TODO:优化改进
			jumpData.setJumped(true);
		}
	}

	//TODO:优化改进
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		JumpData jumpData = ParkourContext.get(player).jumpData();
		if (player.onGround() && jumpData.isJumped()) {
			jumpData.setJumped(false);
		}
	}
}
