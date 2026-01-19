package mod.arcomit.nimblesteps.event.skills;

import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 浅水游泳处理器。
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class ShallowSwimmingHandler {

	@SubscribeEvent
	public static void tryShallowSwimming(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (!canShallowSwim(player, state)) {
			return;
		}

		if (player instanceof LocalPlayer localPlayer) {
			handleLocalShallowSwimming(localPlayer);
		} else {
			handleServerShallowSwimming(player);
		}
	}

	private static void handleLocalShallowSwimming(LocalPlayer localPlayer) {
		if (localPlayer.isSwimming()) {
			return;
		}

		boolean wantsSprint = Minecraft.getInstance().options.keySprint.isDown()
			|| localPlayer.isSprinting();
		if (wantsSprint && localPlayer.hasEnoughFoodToStartSprinting()
			&& localPlayer.input.hasForwardImpulse()) {
			localPlayer.setSprinting(true);
			localPlayer.setSwimming(true);
		}
	}

	private static void handleServerShallowSwimming(Player player) {
		// 服务端逻辑：根据疾跑状态同步游泳状态
		player.setSwimming(player.isSprinting());
	}

	private static boolean canShallowSwim(Player player, NimbleStepsState state) {
		return ServerConfig.enableShallowSwimming
			&& player.isInWater()
			&& PlayerStateUtils.isAbleToAction(player);
	}

}
