package mod.arcomit.parkour.content.action.swimmingjump;

import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.content.mechanic.freestyle.FreestyleHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
public class SwimmingJumpAction {

	public static void execute(Player player) {
		if (!SwimmingJumpEligibilityChecker.check(player)) {
			return;
		}
		Vec3 motion = player.getDeltaMovement();
		player.setDeltaMovement(motion.x, 0.42, motion.z);
	}
}
