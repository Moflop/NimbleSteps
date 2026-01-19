package mod.arcomit.nimblesteps.event.skills;

import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 自由泳与水中跳跃处理器。
 *
 * @author Arcomit
 * @since 2025-12-22
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class FreestyleHandler {

	@SubscribeEvent
	public static void tryFreestyleSwimming(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
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

	public static boolean canFreestyle(Player player, NimbleStepsState state) {
		return ServerConfig.enableFreestyle
			&& player.isSwimming()
			&& !player.isUnderWater()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}
