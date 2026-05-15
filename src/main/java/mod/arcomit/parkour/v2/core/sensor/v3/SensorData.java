package mod.arcomit.parkour.v2.core.sensor.v3;

import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.Map;

public class SensorData {
	private final Map<Direction, CollisionCache> directionCollisionCaches = new EnumMap<>(Direction.class);

	public SensorData() {
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			directionCollisionCaches.put(dir, new CollisionCache());
		}
	}

	public CollisionCache getCollisionCache(Direction direction) {
		CollisionCache cache = directionCollisionCaches.get(direction);
		if (cache == null) {
			throw new IllegalArgumentException("Unsupported direction: " + direction);
		}
		return cache;
	}
}