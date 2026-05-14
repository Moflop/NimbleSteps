package mod.arcomit.parkour.v2.content.handler;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.JumpData;
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
 * @since 2026-04-15
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class JumpStateHandler {

	//TODO:优化改进
	@SubscribeEvent
	public static void onGround(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		JumpData jumpData = ParkourContext.get(player).jumpData();
		WallData wallData = ParkourContext.get(player).wallData();
		if (player.onGround()) {
			if (jumpData.getLastViewWallJumpDir3DData() != -1) {
				jumpData.resetLastLookAngleWallJumpDir3DData();
			}
			if (jumpData.getLastUpWallJumpDir3DData() != -1) {
				jumpData.resetLastUpwardWallJumpDir3DData();
			}
			if (jumpData.getLastParallelWallJumpDir3DData() != -1) {
				jumpData.resetLastForwardWallJumpDir3DData();
			}
			if (wallData.getWallRunCollisionDir3DData() != -1) {
				wallData.setWallRunCollisionDir3DData(-1);
			}
		}
	}
}
