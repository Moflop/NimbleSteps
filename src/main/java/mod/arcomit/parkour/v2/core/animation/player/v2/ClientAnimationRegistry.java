package mod.arcomit.parkour.v2.core.animation.player.v2;

import mod.arcomit.parkour.v2.core.animation.player.IModifierFactory;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientAnimationRegistry {

	private static final Map<ResourceLocation, ParkourAnimation> STATE_ANIMATIONS = new HashMap<>();
	private static final Map<ResourceLocation, IModifierFactory> MODIFIER_FACTORIES = new HashMap<>();

	public static void registerStateAnimation(ResourceLocation stateId, ParkourAnimation animation) {
		STATE_ANIMATIONS.put(stateId, animation);
	}

	public static void registerModifierFactory(ResourceLocation stateId, IModifierFactory factory) {
		MODIFIER_FACTORIES.put(stateId, factory);
	}

	public static ParkourAnimation getAnimation(ResourceLocation stateId) {
		return STATE_ANIMATIONS.get(stateId);
	}

	public static IModifierFactory getModifierFactory(ResourceLocation stateId) {
		return MODIFIER_FACTORIES.get(stateId);
	}
}