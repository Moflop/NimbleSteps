package mod.arcomit.parkour.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-15
 */
public class BoxUtils {

	public static AABB calculateDirectionalCheckBox(LivingEntity livingEntity, Direction direction, double minHeightRatio, double maxHeightRatio, double checkDistance) {
		Vec3 playerPos = livingEntity.position();
		double halfWidth = livingEntity.getBbWidth() / 2;
		double height = livingEntity.getBbHeight();

		AABB baseBox = new AABB(
			playerPos.x - halfWidth,
			playerPos.y + height * minHeightRatio,
			playerPos.z - halfWidth,
			playerPos.x + halfWidth,
			playerPos.y + height * maxHeightRatio,
			playerPos.z + halfWidth
		).deflate(0.001);

		double ox = direction.getStepX() * checkDistance;
		double oy = direction.getStepY() * checkDistance;
		double oz = direction.getStepZ() * checkDistance;

		return baseBox.expandTowards(ox, oy, oz);
	}
}
