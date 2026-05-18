package mod.arcomit.parkour.content.action.walljump;

import mod.arcomit.parkour.utils.CollisionUtils;
import mod.arcomit.parkour.utils.PlayerDirectionUtils;
import mod.arcomit.parkour.content.init.ParkourTags;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class WallJumpTypeResolver {

	public static WallJumpType resolve(Player player, Direction wallDir) {
		if (player == null || wallDir == null) {
			return WallJumpType.NONE;
		}

		if (isBlockInFrontOfPlayer(player)) {
			return WallJumpType.UP;
		}

		if (PlayerDirectionUtils.isLookingInDirectionHorizontal(player, wallDir, 110)) {
			return WallJumpType.PARALLEL;
		}

		return WallJumpType.VIEW;
	}

	public static boolean isBlockInFrontOfPlayer(Player player) {
		return CollisionUtils.isEntityCollidingWithBlockInDirection(
			player, player.getDirection(), ParkourTags.Blocks.SCAFFOLDING_BLOCKS);
	}
}
