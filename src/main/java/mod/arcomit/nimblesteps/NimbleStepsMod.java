package mod.arcomit.nimblesteps;

import com.mojang.logging.LogUtils;
import mod.arcomit.nimblesteps.init.NsAttachmentTypes;
import mod.arcomit.nimblesteps.init.NsSounds;
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
@Mod(NimbleStepsMod.MODID)
public class NimbleStepsMod {
	public static final String MODID = "nimblesteps";
	public static final Logger LOGGER = LogUtils.getLogger();

	public NimbleStepsMod(IEventBus modEventBus, ModContainer modContainer) {
		modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
		NsAttachmentTypes.register(modEventBus);
		NsSounds.register(modEventBus);
		LOGGER.info("NimbleSteps mod initialized!");
	}

	public static ResourceLocation prefix(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
