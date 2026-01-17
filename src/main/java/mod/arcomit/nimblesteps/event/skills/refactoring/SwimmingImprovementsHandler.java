package mod.arcomit.nimblesteps.event.skills.refactoring;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.network.serverbound.jump.ServerboundUseSwimmingJumpPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 游泳改进处理器。
 *
 * @author Arcomit
 * @since 2026-01-07
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class SwimmingImprovementsHandler {

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void checkSwimInterrupt(PlayerTickEvent.Post event) {
		if (!ServerConfig.enableStopSwimmingWhenIdle) {
			return;
		}
		Player player = event.getEntity();
		if (!(player instanceof LocalPlayer localPlayer)) {
			return;
		}

		if (!localPlayer.isSwimming()) {
			return;
		}

		boolean playerStopMove = !localPlayer.input.hasForwardImpulse();
		if (playerStopMove) {
			localPlayer.setSprinting(false);
			localPlayer.setSwimming(false);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void trySwimmingJump(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if (!(player instanceof LocalPlayer localPlayer)) {
			return;
		}
		NimbleStepsState state = NimbleStepsState.getNimbleState(localPlayer);
		if (!canSwimmingJump(localPlayer, state)) {
			return;
		}

		applySwimmingJumpMovement(localPlayer);
		PacketDistributor.sendToServer(new ServerboundUseSwimmingJumpPacket());
	}

	public static void applySwimmingJumpMovement(Player player) {
		Vec3 motion = player.getDeltaMovement();
		boolean isFalling = motion.y < 0;
		if (isFalling) {
			return;
		}

		player.setDeltaMovement(motion.x, 0.42, motion.z);
	}

	private static boolean canSwimmingJump(LocalPlayer player, NimbleStepsState state) {
		return player.jumping
			&& FreestyleHandler.canFreestyle(player, state);
	}
}
