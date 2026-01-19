package mod.arcomit.nimblesteps.utils;

import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * 碰撞检测工具类。
 *
 * @author Arcomit
 * @since 2026-01-01
 */
public class CollisionUtils {
	private static final double DEFAULT_ENTITY_COLLISION_CHECK_RANGE = 0.05;

	/**
	 * 检测实体的碰撞箱在指定方向上是否与忽略标签外的方块发生方块碰撞。
	 *
	 * @param entity    要检测的实体。
	 * @param direction 检测的方向。
	 * @param ignoredBlocksTag 要忽略的方块标签。
	 * @return 如果在指定方向上有固体方块阻挡，则返回 true。
	 */
	public static boolean isEntityCollidingWithBlockInDirection(
		LivingEntity entity,
		Direction direction,
		TagKey<Block> ignoredBlocksTag)
	{
		return isEntityCollidingWithBlockInDirection(
			entity, direction, DEFAULT_ENTITY_COLLISION_CHECK_RANGE, ignoredBlocksTag);
	}

	/**
	 * 检测实体的碰撞箱在指定方向上是否与忽略标签外的方块发生方块碰撞。
	 *
	 * @param entity    要检测的实体。
	 * @param direction 检测的方向。
	 * @param checkRange  检测的距离。
	 * @param ignoredBlocksTag 要忽略的方块标签。
	 * @return 如果在指定方向上有固体方块阻挡，则返回 true。
	 */
	public static boolean isEntityCollidingWithBlockInDirection(
		LivingEntity entity,
		Direction direction,
		double checkRange,
		TagKey<Block> ignoredBlocksTag)
	{
		return isCollidingWithBlockInDirection(
			entity.level(), entity.getBoundingBox(), direction, checkRange, ignoredBlocksTag);
	}

	/**
	 * 检测一组包围盒在指定方向上是否全部与忽略标签外的方块发生方块碰撞。
	 *
	 * @param level       所在的世界。
	 * @param boxes       要检测的包围盒列表。
	 * @param direction   检测的方向。
	 * @param checkRange  检测的距离。
	 * @param ignoredBlocksTag 要忽略的方块标签。
	 * @return 如果在指定方向上与固体方块碰撞，则返回 true。
	 */
	public static boolean areAllBoxesCollidingWithBlockInDirection(
		Level level,
		List<AABB> boxes,
		Direction direction,
		double checkRange,
		TagKey<Block> ignoredBlocksTag)
	{
		return boxes.stream().allMatch(box ->
			CollisionUtils.isCollidingWithBlockInDirection(
				level,
				box,
				direction,
				checkRange,
				ignoredBlocksTag
			)
		);
	}

	/**
	 * 检测指定包围盒在指定方向上是否发生方块碰撞。
	 *
	 * @param level       所在的世界。
	 * @param boundingBox 要检测的包围盒。
	 * @param direction   检测的方向。
	 * @param checkRange  检测的距离。
	 * @param ignoredBlocksTag 要忽略的方块标签。
	 * @return 如果在指定方向上与固体方块碰撞，则返回 true。
	 */
	public static boolean isCollidingWithBlockInDirection(
		Level level,
		AABB boundingBox,
		Direction direction,
		double checkRange,
		TagKey<Block> ignoredBlocksTag)
	{
		double offsetX = direction.getStepX() * checkRange;
		double offsetY = direction.getStepY() * checkRange;
		double offsetZ = direction.getStepZ() * checkRange;
		AABB expandedBox = boundingBox.expandTowards(offsetX, offsetY, offsetZ);
		return isBlockCollision(level, expandedBox, ignoredBlocksTag);
	}

	/**
	 * 检测实体的碰撞箱是否与忽略标签外的任何方块发生方块碰撞。
	 *
	 * @param entity 要检测的实体。
	 * @param ignoredBlocksTag 要忽略的方块标签。
	 * @return 如果有固体方块阻挡，则返回 true。
	 */
	public static boolean isEntityCollidingWithBlock(
		LivingEntity entity,
		TagKey<Block> ignoredBlocksTag)
	{
		return isBlockCollision(
			entity.level(),
			entity.getBoundingBox(),
			ignoredBlocksTag);
	}

	/**
	 * 检测指定包围盒是否与忽略标签以外的任何方块发生碰撞。
	 *
	 * @param level        所在的世界。
	 * @param collisionBox 要检测的包围盒。
	 * @param ignoredBlocksTag 要忽略的方块标签。
	 * @return 如果与任何方块碰撞，则返回 true。
	 */
	public static boolean isBlockCollision(
		Level level,
		AABB collisionBox,
		TagKey<Block> ignoredBlocksTag)
	{
		for (VoxelShape blockCollisionShape : getFilteredBlockCollisions(level, collisionBox, ignoredBlocksTag)) {
			if (!blockCollisionShape.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取过滤后的方块碰撞体。
	 *
	 * @param level       世界。
	 * @param boundingBox 包围盒。
	 * @param ignoredBlocksTag 要忽略的方块标签。
	 * @return 过滤后的碰撞体集合。
	 */
	public static Iterable<VoxelShape> getFilteredBlockCollisions(Level level, AABB boundingBox, TagKey<Block> ignoredBlocksTag) {
		return () ->
			new FilteredBlockCollisions<>(
				level,
				null,
				boundingBox,
				false,
				ignoredBlocksTag,
				(blockPos, shape) -> shape);
	}
}
