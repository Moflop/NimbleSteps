package mod.arcomit.parkour;

import com.mojang.logging.LogUtils;
import mod.arcomit.parkour.content.init.ParkourAttachmentTypes;
import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.content.init.ParkourSounds;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * 模组主类。
 *
 * @author Mitok
 * @since 2026-01-01
 */
@Mod(ParkourMod.MODID)
public class ParkourMod {
	public static final String MODID = "parkour";
	public static final Logger LOGGER = LogUtils.getLogger();

	public ParkourMod(IEventBus modEventBus, ModContainer modContainer) {
		modContainer.registerConfig(ModConfig.Type.SERVER, ParkourConfig.SPEC);
		ParkourAttachmentTypes.register(modEventBus);
		ParkourStates.register(modEventBus);
		ParkourSounds.register(modEventBus);
		LOGGER.info("Parkour mod initialized!");
	}

	public static ResourceLocation prefix(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
