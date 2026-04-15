package mod.arcomit.parkour.v2.core.animation.player;

import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

@OnlyIn(Dist.CLIENT)
public class ClientAnimationRegistry {

	private static final Map<ResourceLocation, IntFunction<PlayerAnimation>> STATE_ANIMATIONS = new HashMap<>();
	private static final Map<ResourceLocation, IModifierFactory> MODIFIER_FACTORIES = new HashMap<>();
	private static final Map<ResourceLocation, Function<AbstractClientPlayer, AbstractModifier>> ACTION_MODIFIERS = new HashMap<>();

	public static void registerStateAnimation(ResourceLocation stateId, IntFunction<PlayerAnimation> animProvider) {
		STATE_ANIMATIONS.put(stateId, animProvider);
	}

	public static void registerModifierFactory(ResourceLocation stateId, IModifierFactory factory) {
		MODIFIER_FACTORIES.put(stateId, factory);
	}

	public static PlayerAnimation getAnimation(ResourceLocation stateId, int variant) {
		IntFunction<PlayerAnimation> provider = STATE_ANIMATIONS.get(stateId);
		return provider != null ? provider.apply(variant) : null;
	}

	public static IModifierFactory getModifierFactory(ResourceLocation stateId) {
		return MODIFIER_FACTORIES.get(stateId);
	}

	public static void registerActionModifier(ResourceLocation actionId, Function<AbstractClientPlayer, AbstractModifier> factory) {
		ACTION_MODIFIERS.put(actionId, factory);
	}

	public static AbstractModifier getActionModifier(ResourceLocation actionId, AbstractClientPlayer player) {
		Function<AbstractClientPlayer, AbstractModifier> factory = ACTION_MODIFIERS.get(actionId);
		return factory != null ? factory.apply(player) : null;
	}
}