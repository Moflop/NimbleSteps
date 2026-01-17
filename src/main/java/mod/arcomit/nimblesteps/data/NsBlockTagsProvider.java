package mod.arcomit.nimblesteps.data;

import mod.arcomit.nimblesteps.init.NsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-01-04
 */
public class NsBlockTagsProvider extends BlockTagsProvider {
	public NsBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		BuiltInRegistries.BLOCK.stream()
			.filter(block -> isCrossCollisionBlock(block)
				|| isNotFullRotatedPillarBlock(block)
				|| isEndRodBlock(block))
			.forEach(this.tag(NsTags.Blocks.CLIMBABLE)::add);
		this.tag(NsTags.Blocks.CLIMBABLE).addTag(BlockTags.FENCES);
		this.tag(NsTags.Blocks.COMMON_IGNORED_BLOCKS).addTag(NsTags.Blocks.CLIMBABLE);
		this.tag(NsTags.Blocks.COMMON_IGNORED_BLOCKS).addTag(BlockTags.CLIMBABLE);
		this.tag(NsTags.Blocks.SCAFFOLDING_BLOCKS).add(Blocks.SCAFFOLDING);
	}

	private static boolean isCrossCollisionBlock(Block block) {
		return block instanceof CrossCollisionBlock;
	}

	private static boolean isNotFullRotatedPillarBlock(Block block) {
		return block instanceof ChainBlock;
	}

	private static boolean isEndRodBlock(Block block) {
		return block instanceof EndRodBlock;
	}
}