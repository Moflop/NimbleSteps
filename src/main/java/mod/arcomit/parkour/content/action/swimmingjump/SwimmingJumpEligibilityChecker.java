package mod.arcomit.parkour.content.action.swimmingjump;

import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.content.mechanic.freestyle.FreestyleHandler;
import mod.arcomit.parkour.core.statemachine.state.IParkourState;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-29
 */
public class SwimmingJumpEligibilityChecker {
	public static boolean check(Player player) {
		return player.jumping && FreestyleHandler.canFreestyle(player);
	}
}
