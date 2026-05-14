package mod.arcomit.parkour.v2.content.action.walljump;

import mod.arcomit.parkour.v1.utils.CollisionUtils;
import mod.arcomit.parkour.v1.utils.PlayerDirectionUtils;
import mod.arcomit.parkour.v2.content.init.ParkourTags;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class WallJumpTypeResolver {

	public static JumpType resolve(Player player, Direction wallDir) {
		if (player == null || wallDir == null) {
			return JumpType.NONE;
		}

		if (isBlockInFrontOfPlayer(player)) {
			return JumpType.UP;
		}

		if (PlayerDirectionUtils.isLookingInDirection_Horizontal(player, wallDir, 110)) {
			return JumpType.PARALLEL;
		}

		return JumpType.VIEW;
	}

	public static boolean isBlockInFrontOfPlayer(Player player) {
		return CollisionUtils.isEntityCollidingWithBlockInDirection(
			player, player.getDirection(), ParkourTags.Blocks.COMMON_IGNORED_BLOCKS);
	}
}
