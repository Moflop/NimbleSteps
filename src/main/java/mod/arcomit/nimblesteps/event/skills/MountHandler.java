package mod.arcomit.nimblesteps.event.skills;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.network.serverbound.jump.ServerboundMountPacket;
import mod.arcomit.nimblesteps.utils.CollisionUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 支撑上墙处理器。
 *
 * @author Arcomit
 * @since 2026-01-18
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class MountHandler {

	@SubscribeEvent
	public static void tryMount(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer localPlayer)) {
			return;
		}
		NimbleStepsState state = NimbleStepsState.getNimbleState(localPlayer);

		boolean jumpNotPressed = !localPlayer.input.jumping;
		if (jumpNotPressed) {
			return;
		}

		if (!canMount(localPlayer, state)) {
			return;
		}
	}

	private static boolean isFacingArmHangingDirection(Player player, NimbleStepsState state) {
		Direction armhangDir = Direction.from3DDataValue(state.getArmHangingDirection());
		Direction facing = player.getDirection();
		return armhangDir == facing;
	}

	public static boolean canMount(Player player, NimbleStepsState state) {
		return ServerConfig.enableMount
			&& state.isArmHanging()
			&& isFacingArmHangingDirection(player, state);
	}
}
