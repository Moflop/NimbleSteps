package mod.arcomit.parkour.content.action.walljump;

import mod.arcomit.parkour.core.context.JumpData;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class WallJumpSameWallGuard {
	static boolean isSameWallAsLastJump(JumpData data, WallJumpType type, Direction wallDir) {
		Direction lastDir = switch (type) {
			case UP -> data.getLastUpWallJumpDir();
			case PARALLEL -> data.getLastParallelWallJumpDir();
			case VIEW -> data.getLastViewWallJumpDir();
			default -> null; // NONE 不可能到达这里
		};
		return wallDir == lastDir;
	}

	static boolean isAgainstSingleWall(Player player) {
		return WallJumpCollisionFinder.findCollisionDirs(player).size() == 1;
	}

	static void recordLastJumpWall(JumpData data, WallJumpType type, Direction wallDir) {
		switch (type) {
			case UP -> data.setLastUpWallJumpDir(wallDir);
			case PARALLEL -> data.setLastParallelWallJumpDir(wallDir);
			case VIEW -> {
				data.setLastViewWallJumpDir(wallDir);
				data.setLastParallelWallJumpDir(wallDir); // VIEW 同时更新 PARALLEL 记录
			}
		}
	}
}
