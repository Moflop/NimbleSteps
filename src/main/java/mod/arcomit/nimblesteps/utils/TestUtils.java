package mod.arcomit.nimblesteps.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-01-19
 */
public class TestUtils {
//
//	@Nullable
//	public static Vec3 getVaultableStep(LivingEntity entity) {
//		final double d = entity.getBbWidth() * 0.5;
//		Level world = entity.getCommandSenderWorld();
//		double distance = entity.getBbWidth() / 2;
//		double baseLine = Math.min(entity.getBbHeight() * 0.86, getWallHeight(entity));
//		double stepX = 0;
//		double stepZ = 0;
//		Vec3 pos = entity.position();
//
//		AABB baseBoxBottom = new AABB(
//			pos.x() - d,
//			pos.y(),
//			pos.z() - d,
//			pos.x() + d,
//			pos.y() + baseLine,
//			pos.z() + d
//		);
//		AABB baseBoxTop = new AABB(
//			pos.x() - d,
//			pos.y() + baseLine,
//			pos.z() - d,
//			pos.x() + d,
//			pos.y() + baseLine + entity.getBbHeight(),
//			pos.z() + d
//		);
//		if (!world.noCollision(entity, baseBoxBottom.expandTowards(distance, 0, 0)) && world.noCollision(entity, baseBoxTop.expandTowards((distance + 1.8), 0, 0))) {
//			stepX++;
//		}
//		if (!world.noCollision(entity, baseBoxBottom.expandTowards(-distance, 0, 0)) && world.noCollision(entity, baseBoxTop.expandTowards(-(distance + 1.8), 0, 0))) {
//			stepX--;
//		}
//		if (!world.noCollision(entity, baseBoxBottom.expandTowards(0, 0, distance)) && world.noCollision(entity, baseBoxTop.expandTowards(0, 0, (distance + 1.8)))) {
//			stepZ++;
//		}
//		if (!world.noCollision(entity, baseBoxBottom.expandTowards(0, 0, -distance)) && world.noCollision(entity, baseBoxTop.expandTowards(0, 0, -(distance + 1.8)))) {
//			stepZ--;
//		}
//		if (stepX == 0 && stepZ == 0) return null;
//		if (stepX == 0 || stepZ == 0) {
//			Vec3 result = new Vec3(stepX, 0, stepZ);
//			Vec3 blockPosition = entity.position().add(result).add(0, 0.5, 0);
//			BlockPos target = new BlockPos(Mth.floor(blockPosition.x()), Mth.floor(blockPosition.y()), Mth.floor(blockPosition.z()));
//			if (!world.isLoaded(target)) return null;
//			BlockState state = world.getBlockState(target);
//			if (state.getBlock() instanceof StairBlock) {
//				Half half = state.getValue(StairBlock.HALF);
//				if (half != Half.BOTTOM) return result;
//				Direction direction = state.getValue(StairBlock.FACING);
//				if (stepZ > 0 && direction == Direction.SOUTH) return null;
//				if (stepZ < 0 && direction == Direction.NORTH) return null;
//				if (stepX > 0 && direction == Direction.EAST) return null;
//				if (stepX < 0 && direction == Direction.WEST) return null;
//			}
//		}
//
//		return new Vec3(stepX, 0, stepZ);
//	}
}
