package mod.arcomit.nimblesteps.v1.utils;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 方向相关工具类。
 *
 * @author Arcomit
 * @since 2026-01-01
 */
public class DirectionUtils {

	/**
	 * 提取列表中离玩家最近位置有方块的方向。
	 *
	 * @param player        玩家实体。
	 * @param directionList 要提取的方向列表。
	 * @return 离玩家最近位置有方块的方向，如果列表为空则返回 null。
	 */
	public static Direction getClosestDirection(Player player, List<Direction> directionList) {
		Direction closestDirection = null;
		double closestDistanceSq = Double.MAX_VALUE;
		Vec3 playerPosition = player.position();
		BlockPos playerBlockPos = player.blockPosition();

		for (Direction direction : directionList) {
			// 使用检测距离来确定方块位置
			BlockPos neighborBlockPos = playerBlockPos.relative(direction);

			// 计算水平距离平方
			double deltaX = playerPosition.x - neighborBlockPos.getCenter().x;
			double deltaZ = playerPosition.z - neighborBlockPos.getCenter().z;
			double distanceSq = deltaX * deltaX + deltaZ * deltaZ;

			if (distanceSq < closestDistanceSq) {
				closestDistanceSq = distanceSq;
				closestDirection = direction;
			}
		}
		return closestDirection;
	}
}
