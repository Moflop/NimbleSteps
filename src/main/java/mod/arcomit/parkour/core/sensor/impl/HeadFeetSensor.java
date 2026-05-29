package mod.arcomit.parkour.core.sensor.impl;

import mod.arcomit.parkour.utils.BoxUtils;
import mod.arcomit.parkour.utils.CollisionUtils;
import mod.arcomit.parkour.content.init.ParkourTags;
import mod.arcomit.parkour.core.sensor.CollisionCache;
import mod.arcomit.parkour.core.sensor.SensorData;
import mod.arcomit.parkour.core.sensor.SensorDataManager;
import mod.arcomit.parkour.core.sensor.SensorType;
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

	public static boolean isValidCollision(Player player, Direction direction) {
		SensorData data = SensorDataManager.get(player).getData(SensorType.HEAD_FEET);
		CollisionCache collisionCache = data.getCollisionCache(direction);
		updateCacheIfNeeded(collisionCache, player, direction);

		return collisionCache.isCollided();
	}

	public static void updateCacheIfNeeded(CollisionCache collisionCache, Player player, Direction direction) {
		long currentTick = player.tickCount;
		Vec3 currentPos = player.position();

		boolean tickInvalid = !collisionCache.isValidTick(currentTick);
		boolean positionInvalid = !collisionCache.isValidPosition(currentPos);

		if (!tickInvalid && !positionInvalid) {
			return; // 缓存完全有效，直接退出（若原来的外部调用允许无操作返回）
		}

		// tick 无效时必须更新
		collisionCache.setTick(currentTick);

		// 位置改变：重新生成盒子并直接做碰撞检测
		if (positionInvalid) {
			collisionCache.setPosition(currentPos);

			List<AABB> boxes = new ArrayList<>();
			boxes.add(BoxUtils.calculateDirectionalCheckBox(player, direction,
				HEAD_BOX_MIN_HEIGHT_RATIO, HEAD_BOX_MAX_HEIGHT_RATIO, COLLISION_CHECK_DISTANCE));
			boxes.add(BoxUtils.calculateDirectionalCheckBox(player, direction,
				FEET_BOX_MIN_HEIGHT_RATIO, FEET_BOX_MAX_HEIGHT_RATIO, COLLISION_CHECK_DISTANCE));
			collisionCache.setCollisionBoxes(boxes);

			// 直接使用刚创建的列表进行检测，并返回
			for (AABB box : boxes) {
				if (!CollisionUtils.isBlockCollision(player.level(), box, IGNORED_BLOCKS_TAG)) {
					collisionCache.setCollided(false);
					return;
				}
			}
			collisionCache.setCollided(true);
			return;
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
