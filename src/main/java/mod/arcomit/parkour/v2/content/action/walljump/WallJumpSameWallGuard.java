package mod.arcomit.parkour.v2.content.action.walljump;

import mod.arcomit.parkour.v2.core.context.JumpData;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class WallJumpSameWallGuard {
	static boolean isSameWallAsLastJump(JumpData data, JumpType type, int wallDirValue) {
		int lastDir = switch (type) {
			case UP -> data.getLastUpWallJumpDir3DData();
			case PARALLEL -> data.getLastParallelWallJumpDir3DData();
			case VIEW -> data.getLastViewWallJumpDir3DData();
			default -> -1; // NONE 不可能到达这里
		};
		return wallDirValue == lastDir;
	}

	static boolean isAgainstSingleWall(Player player) {
		return WallJumpLogic.findCollisionDirs(player).size() == 1;
	}

	static void recordLastJumpWall(JumpData data, JumpType type, int wallDirValue) {
		switch (type) {
			case UP -> data.setLastUpWallJumpDir3DData(wallDirValue);
			case PARALLEL -> data.setLastParallelWallJumpDir3DData(wallDirValue);
			case VIEW -> {
				data.setLastViewWallJumpDir3DData(wallDirValue);
				data.setLastParallelWallJumpDir3DData(wallDirValue); // VIEW 同时更新 PARALLEL 记录
			}
		}
	}
}
