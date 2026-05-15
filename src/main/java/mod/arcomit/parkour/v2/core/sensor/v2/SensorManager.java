package mod.arcomit.parkour.v2.core.sensor.v2;

import mod.arcomit.parkour.v2.core.sensor.v2.impl.HeadFeetSensor;
import mod.arcomit.parkour.v2.core.sensor.v2.impl.WallJumpSensor;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class SensorManager {
	public static final Map<SensorType, ISensor> SENSOR_REGISTRY = new HashMap<>();
	private static final WeakHashMap<Player, Map<SensorType, CollisionSnapshot>> COLLISION_CACHE = new WeakHashMap<>();

	public record CollisionSnapshot(long tick, Vec3 position, List<AABB> cacheBoxs, boolean collided) {}

	public static boolean hasHeadFootCollisionInHorizontal(Player player) {
		return getCollisionSnapshot(player, SensorType.HEAD_FEET_NORTH).collided
			|| getCollisionSnapshot(player, SensorType.HEAD_FEET_SOUTH).collided
			|| getCollisionSnapshot(player, SensorType.HEAD_FEET_WEST).collided
			|| getCollisionSnapshot(player, SensorType.HEAD_FEET_EAST).collided;
	}
	
	public static boolean hasHeadFootCollisionInLeftRight(Player player) {
		Direction facing = player.getDirection();
		if (facing == Direction.NORTH) {
			return getCollisionSnapshot(player, SensorType.HEAD_FEET_WEST).collided || getCollisionSnapshot(player, SensorType.HEAD_FEET_EAST).collided;
		} else if (facing == Direction.SOUTH) {
			return getCollisionSnapshot(player, SensorType.HEAD_FEET_WEST).collided || getCollisionSnapshot(player, SensorType.HEAD_FEET_EAST).collided;
		} else if (facing == Direction.WEST) {
			return getCollisionSnapshot(player, SensorType.HEAD_FEET_NORTH).collided || getCollisionSnapshot(player, SensorType.HEAD_FEET_SOUTH).collided;
		} else if (facing == Direction.EAST) {
			return getCollisionSnapshot(player, SensorType.HEAD_FEET_NORTH).collided || getCollisionSnapshot(player, SensorType.HEAD_FEET_SOUTH).collided;
		}
		return false;
	}

	public static boolean hasHeadFootCollisionInFront(Player player) {
		Direction facing = player.getDirection();
		if (facing == Direction.NORTH) {
			return getCollisionSnapshot(player, SensorType.HEAD_FEET_NORTH).collided;
		} else if (facing == Direction.SOUTH) {
			return getCollisionSnapshot(player, SensorType.HEAD_FEET_SOUTH).collided;
		} else if (facing == Direction.WEST) {
			return getCollisionSnapshot(player, SensorType.HEAD_FEET_WEST).collided;
		} else if (facing == Direction.EAST) {
			return getCollisionSnapshot(player, SensorType.HEAD_FEET_EAST).collided;
		}
		return false;
	}

	public static boolean hasWallJumpCollision(Player player) {
		return getCollisionSnapshot(player, SensorType.WALL_JUMP_NORTH).collided
			|| getCollisionSnapshot(player, SensorType.WALL_JUMP_SOUTH).collided
			|| getCollisionSnapshot(player, SensorType.WALL_JUMP_WEST).collided
			|| getCollisionSnapshot(player, SensorType.WALL_JUMP_EAST).collided;
	}

	public static boolean hasWallJumpCollisionInDirection(Player player, Direction direction) {
		if (direction == Direction.NORTH) {
			return getCollisionSnapshot(player, SensorType.WALL_JUMP_NORTH).collided;
		}
		if (direction == Direction.SOUTH) {
			return getCollisionSnapshot(player, SensorType.WALL_JUMP_SOUTH).collided;
		}
		if (direction == Direction.WEST) {
			return getCollisionSnapshot(player, SensorType.WALL_JUMP_WEST).collided;
		}
		if (direction == Direction.EAST) {
			return getCollisionSnapshot(player, SensorType.WALL_JUMP_EAST).collided;
		}
		return false;
	}
	
	public static CollisionSnapshot getCollisionSnapshot(Player player, SensorType type) {
		Map<SensorType, CollisionSnapshot> playerCache =
			COLLISION_CACHE.computeIfAbsent(player, k -> new HashMap<>());

		CollisionSnapshot cached = playerCache.get(type);

		if (cached == null
			|| cached.tick != player.tickCount
			|| !cached.position.equals(player.position())) {

			List<AABB> boxs = SENSOR_REGISTRY.get(type).getRenderBoxes(player);
			boolean collided = SENSOR_REGISTRY.get(type).isColliding(player);
			cached = new CollisionSnapshot(player.tickCount, player.position(), boxs, collided);
			playerCache.put(type, cached);
		}

		return cached;
	}

	static {
		SENSOR_REGISTRY.put(SensorType.HEAD_FEET_NORTH, new HeadFeetSensor(Direction.NORTH));
		SENSOR_REGISTRY.put(SensorType.HEAD_FEET_SOUTH, new HeadFeetSensor(Direction.SOUTH));
		SENSOR_REGISTRY.put(SensorType.HEAD_FEET_WEST, new HeadFeetSensor(Direction.WEST));
		SENSOR_REGISTRY.put(SensorType.HEAD_FEET_EAST, new HeadFeetSensor(Direction.EAST));

		SENSOR_REGISTRY.put(SensorType.WALL_JUMP_NORTH, new WallJumpSensor(Direction.NORTH));
		SENSOR_REGISTRY.put(SensorType.WALL_JUMP_SOUTH, new WallJumpSensor(Direction.SOUTH));
		SENSOR_REGISTRY.put(SensorType.WALL_JUMP_WEST, new WallJumpSensor(Direction.WEST));
		SENSOR_REGISTRY.put(SensorType.WALL_JUMP_EAST, new WallJumpSensor(Direction.EAST));
	}
}