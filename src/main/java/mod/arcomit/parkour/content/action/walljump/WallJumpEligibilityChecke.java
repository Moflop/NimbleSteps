package mod.arcomit.parkour.content.action.walljump;

import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.core.statemachine.state.IParkourState;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class WallJumpEligibilityChecke {
	public static boolean check(Player player, IParkourState currentState) {
		if (currentState == ParkourStates.WALL_RUN.get() || currentState == ParkourStates.WALL_SLIDE.get()) {
			return true; // TODO: 垂挂
		}else if (currentState == ParkourStates.DEFAULT.get()) {
			if (!player.onGround() && player.getDeltaMovement().y() < 0) {
				return true;
			}else if (player.onClimbable()) {
				return true;
			}
		}else if (currentState == ParkourStates.WALL_CLIMB.get()) {
			if (!player.onGround()) {
				return true;
			}
		}
		return false;
	}
}
