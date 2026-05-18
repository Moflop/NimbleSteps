package mod.arcomit.parkour.content.action.walljump;

import mod.arcomit.parkour.utils.DirectionUtils;
import mod.arcomit.parkour.core.sensor.impl.JumpWallSensor;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-10
 */
public class WallJumpCollisionFinder {

	static List<Direction> findCollisionDirs(Player player) {
		return Direction.Plane.HORIZONTAL.stream()
			.filter(dir -> JumpWallSensor.isColliding(player, dir))
			.collect(Collectors.toList());
	}

	static Direction findClosestCollisionDir(Player player) {
		List<Direction> collidedDirs = findCollisionDirs(player);
		return DirectionUtils.getClosestDirection(player, collidedDirs);
	}


}
