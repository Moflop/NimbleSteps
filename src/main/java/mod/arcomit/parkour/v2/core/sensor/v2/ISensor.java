package mod.arcomit.parkour.v2.core.sensor.v2;

import mod.arcomit.parkour.v2.core.sensor.v2.client.handler.DebugType;
import mod.arcomit.parkour.v2.core.sensor.v2.client.handler.SensorDebugRenderHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public interface ISensor {
	WeakHashMap<Player, Map<Direction, SensorSnapshot>> COLLISION_CACHE = new WeakHashMap<>();

	record SensorSnapshot(Direction direction, long tick, Vec3 position, List<AABB> cacheBoxs, boolean collided) {}

	List<AABB> getRenderBoxes(Player player);
	boolean isColliding(Player player);

	default boolean shouldDebugRender(Player player) {
		return SensorDebugRenderHandler.DEBUG_TYPE == DebugType.ALL;
	}

	static AABB calculateWorldBox(Player player, Direction direction, double minHeightRaito, double maxHeightRaito, double checkDistance) {
		Vec3 playerPos = player.position();
		double halfWidth = player.getBbWidth() / 2;
		double height = player.getBbHeight();

		AABB baseBox = new AABB(
			playerPos.x - halfWidth,
			playerPos.y + height * minHeightRaito,
			playerPos.z - halfWidth,
			playerPos.x + halfWidth,
			playerPos.y + height * maxHeightRaito,
			playerPos.z + halfWidth
		).deflate(0.001);

		double ox = direction.getStepX() * checkDistance;
		double oy = direction.getStepY() * checkDistance;
		double oz = direction.getStepZ() * checkDistance;

		return baseBox.expandTowards(ox, oy, oz);
	}
}
