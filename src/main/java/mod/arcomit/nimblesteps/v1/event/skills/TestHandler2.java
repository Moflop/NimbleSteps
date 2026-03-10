package mod.arcomit.nimblesteps.v1.event.skills;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-02-19
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class TestHandler2 {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		Level level = player.level();
		boolean isColliding = !level.noCollision(player, player.getBoundingBox().deflate(0.001));
		if (isColliding) {
			System.out.println("collision");
		}else  {
			System.out.println("no collision");
		}
	}
}
