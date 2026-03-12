package mod.arcomit.parkour.v1.utils;

import com.google.common.collect.AbstractIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * 过滤了部分方块的碰撞检测迭代器。
 *
 * @author Arcomit
 * @since 2026-01-03
 */
public class FilteredBlockCollisions<T> extends AbstractIterator<T> {
	private static final double EPSILON = 1.0E-7;
	private static final int BLOCK_MARGIN = 1;
	private static final double BLOCK_SIZE = 1.0;

	private final CollisionGetter collisionWorld;
	private final CollisionContext collisionContext;
	private final AABB boundingBox;
	private final VoxelShape boundingShape;
	private final boolean filterSuffocatingBlocks;
	private final TagKey<Block> ignoredBlocksTag;
	private final BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> resultProvider;
	private final Cursor3D blockCursor;
	private final BlockPos.MutableBlockPos mutableBlockPos;

	@Nullable
	private BlockGetter cachedCollisionChunk;
	private long cachedCollisionChunkPos;

	public FilteredBlockCollisions(
		CollisionGetter collisionWorld,
		@Nullable Entity entity,
		AABB boundingBox,
		boolean filterSuffocatingBlocks,
		TagKey<Block> ignoredBlocksTag,
		BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> resultProvider
	) {
		this.collisionWorld = collisionWorld;
		this.collisionContext = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
		this.boundingBox = boundingBox;
		this.boundingShape = Shapes.create(boundingBox);
		this.filterSuffocatingBlocks = filterSuffocatingBlocks;
		this.ignoredBlocksTag = ignoredBlocksTag;
		this.resultProvider = resultProvider;
		this.mutableBlockPos = new BlockPos.MutableBlockPos();

		int minX = Mth.floor(boundingBox.minX - EPSILON) - BLOCK_MARGIN;
		int maxX = Mth.floor(boundingBox.maxX + EPSILON) + BLOCK_MARGIN;
		int minY = Mth.floor(boundingBox.minY - EPSILON) - BLOCK_MARGIN;
		int maxY = Mth.floor(boundingBox.maxY + EPSILON) + BLOCK_MARGIN;
		int minZ = Mth.floor(boundingBox.minZ - EPSILON) - BLOCK_MARGIN;
		int maxZ = Mth.floor(boundingBox.maxZ + EPSILON) + BLOCK_MARGIN;
		this.blockCursor = new Cursor3D(minX, minY, minZ, maxX, maxY, maxZ);

	}

	@Nullable
	private BlockGetter getChunk(int x, int z) {
		int sectionX = SectionPos.blockToSectionCoord(x);
		int sectionZ = SectionPos.blockToSectionCoord(z);
		long chunkPos = ChunkPos.asLong(sectionX, sectionZ);
		if (this.cachedCollisionChunk != null && this.cachedCollisionChunkPos == chunkPos) {
			return this.cachedCollisionChunk;
		} else {
			BlockGetter chunk = this.collisionWorld.getChunkForCollisions(sectionX, sectionZ);
			this.cachedCollisionChunk = chunk;
			this.cachedCollisionChunkPos = chunkPos;
			return chunk;
		}
	}

	@Override
	protected T computeNext() {
		while (this.blockCursor.advance()) {
			int x = this.blockCursor.nextX();
			int y = this.blockCursor.nextY();
			int z = this.blockCursor.nextZ();
			int cursorType = this.blockCursor.getNextType();
			if (cursorType == Cursor3D.TYPE_CORNER) {
				continue;
			}
			BlockGetter chunk = this.getChunk(x, z);
			if (chunk == null) {
				continue;
			}
			this.mutableBlockPos.set(x, y, z);
			BlockState blockState = chunk.getBlockState(this.mutableBlockPos);

			// 忽略脚手架，后续可能还会禁用其它东西，可以添加一个Tag来控制（TODO）
			if (blockState.is(this.ignoredBlocksTag)) {
				continue;
			}

			boolean suffocatingCheck = !this.filterSuffocatingBlocks || blockState.isSuffocating(chunk, this.mutableBlockPos);
			if (!suffocatingCheck) {
				continue;
			}
			boolean largeCollisionShapeCheck = cursorType != Cursor3D.TYPE_FACE || blockState.hasLargeCollisionShape();
			if (!largeCollisionShapeCheck) {
				continue;
			}
			boolean movingPistonCheck = cursorType != Cursor3D.TYPE_EDGE || blockState.is(Blocks.MOVING_PISTON);
			if (!movingPistonCheck) {
				continue;
			}

			VoxelShape blockShape = blockState.getCollisionShape(this.collisionWorld, this.mutableBlockPos, this.collisionContext);
			if (blockShape == Shapes.block()) {
				if (this.boundingBox.intersects(x, y, z, x + BLOCK_SIZE, y + BLOCK_SIZE, z + BLOCK_SIZE)) {
					return this.resultProvider.apply(this.mutableBlockPos, blockShape.move(x, y, z));
				}
			} else {
				VoxelShape movedShape = blockShape.move(x, y, z);
				if (!movedShape.isEmpty() && Shapes.joinIsNotEmpty(movedShape, this.boundingShape, BooleanOp.AND)) {
					return this.resultProvider.apply(this.mutableBlockPos, movedShape);
				}
			}
		}
		return this.endOfData();
	}
}
