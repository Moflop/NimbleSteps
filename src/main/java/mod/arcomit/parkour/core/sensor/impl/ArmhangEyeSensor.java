package mod.arcomit.parkour.core.sensor.impl;

import mod.arcomit.parkour.content.init.ParkourTags;
import mod.arcomit.parkour.core.sensor.SensorData;
import mod.arcomit.parkour.core.sensor.SensorDataManager;
import mod.arcomit.parkour.core.sensor.SensorType;
import mod.arcomit.parkour.core.sensor.CollisionCache;
import mod.arcomit.parkour.utils.CollisionUtils;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * 专门用于垂挂判定的传感器。
 * 判定逻辑：上半身无方块阻挡，下半身有方块支撑。
 *
 * @author Arcomit
 */
public class ArmhangEyeSensor {
	private static final double COLLISION_CHECK_DISTANCE = 0.5;
	// 底部碰撞检测高度比例，原版玩家站立为1.8格，眼睛高度为1.62，而34%约等于0.6格，确保玩家不会在脚部的位置（一格）触发垂挂
	private static final double BOTTOM_COLLISION_MIN_HEIGHT_RATIO = 0.34;
	private static final TagKey<Block> IGNORED_BLOCKS_TAG = ParkourTags.Blocks.SCAFFOLDING_BLOCKS; // 忽略脚手架等

	public static List<AABB> getBoxes(Player player, Direction direction) {
		SensorData data = SensorDataManager.get(player).getData(SensorType.ARMHANG_EYE);
		CollisionCache collisionCache = data.getCollisionCache(direction);
		updateCacheIfNeeded(collisionCache, player, direction);
		return collisionCache.getCollisionBoxes();
	}

	public static boolean isValidCollision(Player player, Direction direction) {
		SensorData data = SensorDataManager.get(player).getData(SensorType.ARMHANG_EYE);
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

			List<AABB> boxes = buildOffsetBoxes(player, direction, player.getEyeHeight());
			collisionCache.setCollisionBoxes(boxes);

			if (!meetsArmhangCondition(player, boxes)) {
				collisionCache.setCollided(false);
				return;
			}
			collisionCache.setCollided(true);
			return;
		}

		List<AABB> boxes = collisionCache.getCollisionBoxes();
		if (!meetsArmhangCondition(player, boxes)) {
			collisionCache.setCollided(false);
			return;
		}
		collisionCache.setCollided(true);
	}

	private static boolean meetsArmhangCondition(Player player, List<AABB> boxes) {
		AABB bottomBox = boxes.get(0);
		AABB topBox = boxes.get(1);
		boolean bottomHasSupport = CollisionUtils.isBlockCollision(player.level(), bottomBox, IGNORED_BLOCKS_TAG);
		boolean topIsClear = !CollisionUtils.isBlockCollision(player.level(), topBox, IGNORED_BLOCKS_TAG);
		if (bottomHasSupport && topIsClear) {
			return true;
		}
		return false;
	}

	private static List<AABB> buildOffsetBoxes(Player player, Direction direction, double baseHeight) {
		Vec3 pos = player.position();
		double halfWidth = player.getBbWidth() / 2;
		double height = player.getBbHeight();
		double topOffset = height - player.getEyeHeight();

		double minX = pos.x - halfWidth;
		double maxX = pos.x + halfWidth;
		double minZ = pos.z - halfWidth;
		double maxZ = pos.z + halfWidth;
		double baseY = pos.y + baseHeight;

		AABB bottomBox = new AABB(
			minX,
			baseY,
			minZ,
			maxX,
			baseY - (height * BOTTOM_COLLISION_MIN_HEIGHT_RATIO),
			maxZ
		).deflate(0.001);

		AABB topBox = new AABB(
			minX,
			baseY,
			minZ,
			maxX,
			baseY + topOffset,
			maxZ
		).deflate(0.001);

		// 施加方向检查的偏移量
		double dx = direction.getStepX() * COLLISION_CHECK_DISTANCE;
		double dz = direction.getStepZ() * COLLISION_CHECK_DISTANCE;

		List<AABB> boxes = new ArrayList<>();
		boxes.add(bottomBox.move(dx, 0, dz));
		boxes.add(topBox.move(dx, 0, dz));
		return boxes;
	}
}