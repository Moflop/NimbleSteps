package mod.arcomit.parkour.v2.core.sensor.v3.impl;

import mod.arcomit.parkour.v1.utils.BoxUtils;
import mod.arcomit.parkour.v1.utils.CollisionUtils;
import mod.arcomit.parkour.v2.content.init.ParkourTags;
import mod.arcomit.parkour.v2.core.sensor.v3.CollisionCache;
import mod.arcomit.parkour.v2.core.sensor.v3.SensorData;
import mod.arcomit.parkour.v2.core.sensor.v3.SensorDataManager;
import mod.arcomit.parkour.v2.core.sensor.v3.SensorType;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-15
 */
public class HeadFeetSensor {
	private static final double FEET_BOX_MIN_HEIGHT_RATIO = 0.0;
	private static final double FEET_BOX_MAX_HEIGHT_RATIO = 0.3;
	private static final double HEAD_BOX_MIN_HEIGHT_RATIO = 0.85;
	private static final double HEAD_BOX_MAX_HEIGHT_RATIO = 1.0;
	private static final double COLLISION_CHECK_DISTANCE = 0.15;
	private static final TagKey<Block> IGNORED_BLOCKS_TAG = ParkourTags.Blocks.COMMON_IGNORED_BLOCKS;

	public static List<AABB> getBoxes(Player player, Direction direction) {
		SensorData data = SensorDataManager.get(player).getData(SensorType.HEAD_FEET);
		CollisionCache collisionCache = data.getCollisionCache(direction);
		updateCacheIfNeeded(collisionCache, player, direction);

		return collisionCache.getCollisionBoxes();
	}

	public static boolean isColliding(Player player, Direction direction) {
		SensorData data = SensorDataManager.get(player).getData(SensorType.HEAD_FEET);
		CollisionCache collisionCache = data.getCollisionCache(direction);
		updateCacheIfNeeded(collisionCache, player, direction);

		return collisionCache.isCollided();
	}

	public static void updateCacheIfNeeded(CollisionCache collisionCache , Player player, Direction direction) {
		long currentTick = player.tickCount;
		if (!collisionCache.isValidTick(currentTick)) {
			collisionCache.setTick(currentTick);

			Vec3 currentPos = player.position();
			if (!collisionCache.isValidPosition(currentPos)){
				collisionCache.setPosition(currentPos);

				List<AABB> boxes = new ArrayList<>();
				boxes.add(BoxUtils.calculateDirectionalCheckBox(player, direction, HEAD_BOX_MIN_HEIGHT_RATIO, HEAD_BOX_MAX_HEIGHT_RATIO, COLLISION_CHECK_DISTANCE));
				boxes.add(BoxUtils.calculateDirectionalCheckBox(player, direction, FEET_BOX_MIN_HEIGHT_RATIO, FEET_BOX_MAX_HEIGHT_RATIO, COLLISION_CHECK_DISTANCE));
				collisionCache.setCollisionBoxes(boxes);
			}

			List<AABB> boxes = collisionCache.getCollisionBoxes();
			for (AABB box : boxes) {
				if (!CollisionUtils.isBlockCollision(player.level(), box, IGNORED_BLOCKS_TAG)) {
					collisionCache.setCollided(false);
					return;
				}
			}
			collisionCache.setCollided(true);
		}
	}


}
