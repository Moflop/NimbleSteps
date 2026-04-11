package mod.arcomit.parkour.v2.content.init;

import mod.arcomit.parkour.ParkourMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * 模组标签类。
 *
 * @author Mitok
 * @since 2026-01-04
 */
public class PkTags {
	public static class Blocks
	{
		public static final TagKey<Block> CLIMBABLE = createBlockTag("climbable");
		public static final TagKey<Block> COMMON_IGNORED_BLOCKS = createBlockTag("common_ignored_blocks");
		public static final TagKey<Block> SCAFFOLDING_BLOCKS = createBlockTag("scaffolding_blocks");
	}

	private static TagKey<Block> createBlockTag(String name) {
		return TagKey.create(Registries.BLOCK, ParkourMod.prefix(name));
	}
}
