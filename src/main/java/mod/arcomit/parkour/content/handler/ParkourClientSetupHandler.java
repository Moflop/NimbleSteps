package mod.arcomit.parkour.content.handler;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.content.behavior.wallclimb.client.WallClimbPlayerAnimModifier;
import mod.arcomit.parkour.content.behavior.wallrun.client.WallRunPlayerAnimModifier;
import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.content.init.ParkourPlayerAnimations;
import mod.arcomit.parkour.core.client.animation.camera.CameraAnimationRegistry;
import mod.arcomit.parkour.content.behavior.landingroll.client.animation.player.LandingRollPlayerAnimModifier;
import mod.arcomit.parkour.content.behavior.slide.client.SlidePlayerAnimModifier;
import mod.arcomit.parkour.core.client.animation.player.ClientAnimationRegistry;
import mod.arcomit.parkour.content.behavior.wallslide.client.WallSlidePlayerAnimModifier;
import mod.arcomit.parkour.core.proxy.client.*;
import mod.arcomit.parkour.core.proxy.ParkourProxies;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class ParkourClientSetupHandler {

	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ParkourProxies.INPUT_PROXY = new ClientInputProxyImpl();
		ParkourProxies.SOUND_PROXY = new ClientSoundProxyImpl();
		ParkourProxies.PLAYER_ANIM_PROXY = new ClientPlayerAnimProxyImpl();
		ParkourProxies.CAMERA_PROXY = new ClientCameraAnimProxyImpl();
		ParkourProxies.PLAYER_SERVICES_PROXY = new ClientPlayerServicesProxyImpl();
		ParkourProxies.MINECRAFT_PROXY =  new ClientMinecraftProxyImpl();

		event.enqueueWork(() -> {
			ClientAnimationRegistry.registerStateAnimation(ParkourStates.SLIDE.getId(),
				variant -> variant == 1 ? ParkourPlayerAnimations.SLIDE_2 : ParkourPlayerAnimations.SLIDE_1
			);
			ClientAnimationRegistry.registerModifierFactory(ParkourStates.SLIDE.getId(),
				(controller, player, state, variant) -> {
					controller.addModifierLast(new SlidePlayerAnimModifier(player));
				}
			);

			ClientAnimationRegistry.registerStateAnimation(ParkourStates.WALL_SLIDE.getId(),
				variant -> ParkourPlayerAnimations.EMPTY_ANIM);
			ClientAnimationRegistry.registerModifierFactory(ParkourStates.WALL_SLIDE.getId(),
				(controller, player, state, variant) -> {
				controller.addModifierLast(new WallSlidePlayerAnimModifier(player));
				}
			);

			// 注册 State 对应的动画（根据 variant 返回不同的 JSON）
			ClientAnimationRegistry.registerStateAnimation(ParkourStates.WALL_RUN.getId(), variant -> variant == 0 ? ParkourPlayerAnimations.WALL_RUN_LEFT : ParkourPlayerAnimations.WALL_RUN_RIGHT);

			// 注册 Modifier 工厂
			ClientAnimationRegistry.registerModifierFactory(ParkourStates.WALL_RUN.getId(), (controller, player, state, variant) -> {
				boolean isWallOnLeft = (variant == 0);
				controller.addModifierLast(new WallRunPlayerAnimModifier(player, isWallOnLeft));
			});

			ClientAnimationRegistry.registerStateAnimation(
				ParkourStates.WALL_CLIMB.getId(), // 替换为你实际的 State ID
				variant -> ParkourPlayerAnimations.WALL_CLIMB
			);
			// 绑定程序化修改器
			ClientAnimationRegistry.registerModifierFactory(
				ParkourStates.WALL_CLIMB.getId(),
				(controller, player, state, variant) -> {
					controller.addModifierLast(new WallClimbPlayerAnimModifier(player));
				}
			);

			ClientAnimationRegistry.registerActionModifier(ParkourPlayerAnimations.LANDING_ROLL.id, player -> {
				// 指定 16 Tick 总长，最后 6 Tick 过度融合原版
				return new LandingRollPlayerAnimModifier(player, 18, 6);
			});
		});

	}

	@SubscribeEvent
	public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
		// 注册我们的动画加载器
		event.registerReloadListener(CameraAnimationRegistry.INSTANCE);
	}
}