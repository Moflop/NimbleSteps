package mod.arcomit.parkour.v2.content.behavior.wallslide.client;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v1.network.serverbound.wallslide.ServerboundUpdateWallSlideStatePacket;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v2.core.context.WallMovementData;
import mod.arcomit.parkour.v2.content.behavior.slide.WallSlideLogic;
import mod.arcomit.parkour.v2.content.behavior.wallslide.WallSlideState;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class WallSlideClientHandler {

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void tryStartWallSlide(PlayerTickEvent.Post event) {
		boolean jump = event.getEntity().jumping;
		if (event.getEntity().level().isClientSide) {
			System.out.println("CLIENT jump isPressed: " + jump);
		}else {
			System.out.println("SERVER jump isPressed: " + jump);
		}
		if (!(event.getEntity() instanceof LocalPlayer player)) return;

		MovementStateContext state = MovementStateContext.get(player);
		WallMovementData wallData = state.getWallData();

		if (wallData.isWallSliding()) return;

		boolean jumpIsPressed = player.input.jumping;

		System.out.println("jump isPressed: " + jumpIsPressed);

		if (jumpIsPressed && WallSlideLogic.canStartWallSlide(player, state)) {
			WallSlideLogic.startWallSliding(wallData);
			player.sendPosition();
			PacketDistributor.sendToServer(new ServerboundUpdateWallSlideStatePacket(true));
		}
	}

	@SubscribeEvent
	public static void handleWallSlideRelease(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer player)) return;

		MovementStateContext state = MovementStateContext.get(player);
		WallMovementData wallData = state.getWallData();
		ParkourStateMachine stateMachine = ParkourStateMachine.get(player);

		if (stateMachine.getCurrentState() == WallSlideState.INSTANCE) {
			boolean releaseJump = !player.input.jumping;
			if (releaseJump) {
				WallSlideLogic.updateWallSlideGracePeriod(wallData);
				// 如果宽限期结束，wallSliding 被置为 false，我们通知服务端
				if (!wallData.isWallSliding()) {
					PacketDistributor.sendToServer(new ServerboundUpdateWallSlideStatePacket(false));
				}
			}
		}
	}
}