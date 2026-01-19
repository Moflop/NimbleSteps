package mod.arcomit.nimblesteps.utils;

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

	/**
	 * 根据玩家的YRot获取方向向量（支持8方向，包括对角线）
	 * @param yRot 玩家的水平旋转角度
	 * @return 对应的方向向量
	 */
	public static Vec3 getDirectionFromYRot(float yRot) {
		// 标准化角度到 -180° 到 180°
		yRot = Mth.wrapDegrees(yRot);

		int x = 0, z = 0;

		// Z方向：-67.5° 到 67.5° 为 Z+1
		if (yRot >= -67.5f && yRot <= 67.5f) {
			z = 1;
		}
		// Z方向：112.5° 到 180° 或 -180° 到 -112.5° 为 Z-1
		else if (yRot >= 112.5f || yRot <= -112.5f) {
			z = -1;
		}

		// X方向：22.5° 到 157.5° 为 X-1
		if (yRot >= 22.5f && yRot <= 157.5f) {
			x = -1;
		}
		// X方向：-157.5° 到 -22.5° 为 X+1
		else if (yRot >= -157.5f && yRot <= -22.5f) {
			x = 1;
		}

		return new Vec3(x, 0, z);
	}
}
