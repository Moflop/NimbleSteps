package mod.arcomit.parkour.v2.content.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.input.ParkourInputActions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/**
 * 客户端按键绑定管理类。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class ParkourKeyBindings {
	private static final String CATEGORY = "key.categories." + ParkourMod.MODID;

	public static final ParkourKeyMapping SLIDE_KEY = new ParkourKeyMapping(
		"key." + ParkourMod.MODID + ".roll_slide",
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_V,
		CATEGORY,
		ParkourInputActions.SLIDE
	);

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(SLIDE_KEY);
	}
}
