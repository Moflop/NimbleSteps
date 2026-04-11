package mod.arcomit.parkour.v2.content.mechanic.swimimprovements;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v1.network.serverbound.jump.ServerboundUseSwimmingJumpPacket;
import mod.arcomit.parkour.v2.content.action.swimmingjump.SwimmingJumpLogic;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
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
@EventBusSubscriber(modid = ParkourMod.MODID)
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
		ParkourContext state = ParkourContext.get(localPlayer);
		if (!SwimmingJumpLogic.canSwimmingJump(localPlayer, state)) {
			return;
		}

		SwimmingJumpLogic.applySwimmingJumpMovement(localPlayer);
		PacketDistributor.sendToServer(new ServerboundUseSwimmingJumpPacket());
	}

}
