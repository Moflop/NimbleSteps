package mod.arcomit.parkour.content.action.walljump;

import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.context.WallData;
import mod.arcomit.parkour.core.statemachine.state.IParkourState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class WallJumpDirectionResolver {
	static Direction resolveWallDirection(Player player, IParkourState state, ParkourContext context) {
		if (state == ParkourStates.WALL_RUN.get()) {
			WallData wallData = context.wallData();
			return wallData.getWallRunCollisionDir();
		} else if (state == ParkourStates.WALL_SLIDE.get()) {
			WallData wallData = context.wallData();
			return wallData.getWallSlideCollisionDir();
		} else if (state == ParkourStates.DEFAULT.get() || state == ParkourStates.WALL_CLIMB.get()) {
			return WallJumpCollisionFinder.findClosestCollisionDir(player);
		}
		// 为未来垂挂预留扩展点
		// if (state == ParkourStates.ARMHANG.get()) {
		//     return Direction.from3DDataValue(wallData.getArmHangingDir());
		// }
		return null;
	}
}
