package mod.arcomit.parkour.v2.content.mechanic.freestyle;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 自由泳处理器。
 *
 * @author Arcomit
 * @since 2025-12-22
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class FreestyleHandler {

	@SubscribeEvent
	public static void tryFreestyleSwimming(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		MovementStateContext state = MovementStateContext.get(player);
		if (!canFreestyle(player, state)) {
			return;
		}

		Vec3 motion = player.getDeltaMovement();
		boolean isFalling = motion.y < 0;
		if (isFalling) {
			return;
		}

		player.setDeltaMovement(motion.x, 0, motion.z);
	}

	public static boolean canFreestyle(Player player, MovementStateContext state) {
		return ServerConfig.enableFreestyle
			&& player.isSwimming()
			&& !player.isUnderWater()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}
