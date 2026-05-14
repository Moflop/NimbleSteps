package mod.arcomit.parkour.v2.content.action.walljump;

import mod.arcomit.parkour.v1.utils.DirectionUtils;
import mod.arcomit.parkour.v2.content.init.ParkourSounds;
import mod.arcomit.parkour.v2.content.init.ParkourStates;
import mod.arcomit.parkour.v2.core.context.JumpData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.WallData;
import mod.arcomit.parkour.v2.core.sensor.AbstractBoxSensor;
import mod.arcomit.parkour.v2.core.sensor.SensorManager;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-10
 */
public class WallJumpLogic {

	static List<Direction> findCollisionDirs(Player player, SensorManager sensorManager) {
		return Direction.Plane.HORIZONTAL.stream()
			.filter(dir -> checkWallCollision(player, sensorManager, dir))
			.collect(Collectors.toList());
	}

	static Direction findClosestCollisionDir(Player player, SensorManager sm) {
		List<Direction> collidedDirs = findCollisionDirs(player, sm);
		return DirectionUtils.getClosestDirection(player, collidedDirs);
	}

	private static boolean checkWallCollision(Player player, SensorManager sensorManager, Direction dir) {
		AbstractBoxSensor feetSensor = sensorManager.getSensor("feet_wall_" + dir.getName());

		return feetSensor != null
			&& feetSensor.isColliding(player);
	}


}
