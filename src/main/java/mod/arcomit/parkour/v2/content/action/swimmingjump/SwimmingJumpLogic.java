package mod.arcomit.parkour.v2.content.action.swimmingjump;

import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v2.content.mechanic.freestyle.FreestyleHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
public class SwimmingJumpLogic {
	public static void applySwimmingJumpMovement(Player player) {
		Vec3 motion = player.getDeltaMovement();
		boolean isFalling = motion.y < 0;
		if (isFalling) {
			return;
		}

		player.setDeltaMovement(motion.x, 0.42, motion.z);
	}

	public static boolean canSwimmingJump(LocalPlayer player, MovementStateContext state) {
		return player.jumping
			&& FreestyleHandler.canFreestyle(player, state);
	}
}
