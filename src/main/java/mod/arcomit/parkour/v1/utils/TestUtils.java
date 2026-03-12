package mod.arcomit.parkour.v1.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-01-19
 */
public class TestUtils {
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

	public static double getObstaclesHeight(Player player, Vec3 direction) {
		Vec3 playerPos = player.position();
		Level world = player.level();

		final double CHECK_ACCURACY = 0.1;

		// 预先计算不变的边界值，避免循环内重复运算
		double halfWidth = player.getBbWidth() / 2;
		double minX = playerPos.x - halfWidth;
		double maxX = playerPos.x + halfWidth;
		double minZ = playerPos.z - halfWidth;
		double maxZ = playerPos.z + halfWidth;

		// 在玩家高度上方增加0.1以确保垂挂极端情况的障碍物也能检测到
		double baseHeight = player.getBbHeight() + 0.1;

		int loopNum = (int) Math.round(baseHeight / CHECK_ACCURACY);

		for (int i = 0; i < loopNum; i++) {
			// 当前切片的高度（从头顶向下计算）
			double currentHeightLevel = baseHeight - (CHECK_ACCURACY * i);
			double sliceMaxY = playerPos.y + currentHeightLevel;
			double sliceMinY = playerPos.y + baseHeight - (CHECK_ACCURACY * (i + 1));

			AABB sliceBox = new AABB(minX, sliceMinY, minZ, maxX, sliceMaxY, maxZ);
			AABB checkBox = sliceBox.expandTowards(direction);
			if (!world.noCollision(player, checkBox)) {
				return currentHeightLevel;
			}
		}

		// 未检测到障碍物，返回0
		return 0;
	}


	public static boolean hasEnoughSpaceAbove(Player player, Vec3 direction, double baseHeight) {
		Vec3 playerPos = player.position();
		Level world = player.level();

		double halfWidth = player.getBbWidth() / 2;
		double height = player.getBbHeight();

		AABB baseBox = new AABB(
			playerPos.x - halfWidth,
			playerPos.y + baseHeight,
			playerPos.z - halfWidth,
			playerPos.x + halfWidth,
			playerPos.y + baseHeight + height,
			playerPos.z + halfWidth
		);

		AABB checkBox = baseBox.expandTowards(direction);
		if (world.noCollision(player, checkBox)) {
			return true;
		}

		return false;
	}
}
