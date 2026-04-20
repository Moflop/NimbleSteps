package mod.arcomit.parkour;

import mod.arcomit.parkour.v2.content.client.animation.player.modifier.AdaptiveClimbSpeedModifier;
import mod.arcomit.parkour.v2.content.client.animation.player.modifier.ProceduralWallRunModifier;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.content.init.PkPlayerAnimations;
import mod.arcomit.parkour.v2.core.animation.camera.CameraAnimationRegistry;
import mod.arcomit.parkour.v2.content.client.animation.player.modifier.ProceduralLandingRollModifier;
import mod.arcomit.parkour.v2.content.client.animation.player.modifier.ProceduralSlideModifier;
import mod.arcomit.parkour.v2.core.animation.player.ClientAnimationRegistry;
import mod.arcomit.parkour.v2.core.animation.player.modifier.ProceduralWallSlideModifier;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class ParkourClientSetup {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ClientAnimationRegistry.registerStateAnimation(PkParkourStates.SLIDE.getId(),
				variant -> variant == 1 ? PkPlayerAnimations.SLIDE_2 : PkPlayerAnimations.SLIDE_1
			);
			ClientAnimationRegistry.registerModifierFactory(PkParkourStates.SLIDE.getId(),
				(controller, player, state, variant) -> {
					controller.addModifierLast(new ProceduralSlideModifier(player));
				}
			);

			ClientAnimationRegistry.registerStateAnimation(PkParkourStates.WALL_SLIDE.getId(),
				variant -> PkPlayerAnimations.EMPTY_ANIM);
			ClientAnimationRegistry.registerModifierFactory(PkParkourStates.WALL_SLIDE.getId(),
				(controller, player, state, variant) -> {
				controller.addModifierLast(new ProceduralWallSlideModifier(player));
				}
			);

			// 注册 State 对应的动画（根据 variant 返回不同的 JSON）
			ClientAnimationRegistry.registerStateAnimation(PkParkourStates.WALL_RUN.getId(), variant -> variant == 0 ? PkPlayerAnimations.WALL_RUN_LEFT : PkPlayerAnimations.WALL_RUN_RIGHT);

			// 注册 Modifier 工厂
			ClientAnimationRegistry.registerModifierFactory(PkParkourStates.WALL_RUN.getId(), (controller, player, state, variant) -> {
				boolean isWallOnLeft = (variant == 0);
				controller.addModifierLast(new ProceduralWallRunModifier(player, isWallOnLeft));
			});

			ClientAnimationRegistry.registerStateAnimation(
				PkParkourStates.WALL_CLIMB.getId(), // 替换为你实际的 State ID
				variant -> PkPlayerAnimations.WALL_CLIMB
			);
			// 绑定程序化修改器
//			ClientAnimationRegistry.registerModifierFactory(
//				PkParkourStates.WALL_CLIMB.getId(),
//				(controller, player, state, variant) -> {
//					controller.addModifierLast(new AdaptiveClimbSpeedModifier(player));
//				}
//			);

			ClientAnimationRegistry.registerActionModifier(PkPlayerAnimations.LANDING_ROLL.id, player -> {
				// 指定 16 Tick 总长，最后 6 Tick 过度融合原版
				return new ProceduralLandingRollModifier(player, 18, 6);
			});
		});

	}

	@SubscribeEvent
	public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
		// 注册我们的动画加载器
		event.registerReloadListener(CameraAnimationRegistry.INSTANCE);
	}
}