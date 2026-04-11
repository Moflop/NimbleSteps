package mod.arcomit.parkour.v2.core.animation;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.content.init.PkRegistries;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ParkourModifierRegistry {

	// 客户端修饰器注册表
	private static final Map<ResourceLocation, IModifierFactory> MODIFIER_FACTORIES = new HashMap<>();

	/**
	 * 提供给外部的注册方法
	 */
	public static void registerModifierFactory(ResourceLocation stateId, IModifierFactory factory) {
		MODIFIER_FACTORIES.put(stateId, factory);
	}

	/**
	 * 核心装配方法：根据状态从注册表中取出工厂并应用修饰器
	 */
	public static void applyModifiers(PlayerAnimationController controller, AbstractClientPlayer player, IParkourState state, int variant) {
		// 1. 每次应用前清理旧修饰器
		controller.removeAllModifiers();

		if (state == null) return;

		// 2. 查表并应用
		ResourceLocation stateId = PkRegistries.PARKOUR_REGISTRY.getKey(state);
		if (stateId != null) {
			IModifierFactory factory = MODIFIER_FACTORIES.get(stateId);
			if (factory != null) {
				factory.apply(controller, player, state, variant);
			}
		}
	}

	/**
	 * 初始化时注册所有原版自带的修饰器
	 */
	public static void registerAll() {
		// 注册滑铲修饰器
		registerModifierFactory(PkParkourStates.SLIDE.getId(),
			(controller, player, state, variant) -> {
					controller.addModifierLast(new ProceduralSlideModifier(player));
			}
		);
	}
}