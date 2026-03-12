package mod.arcomit.parkour.v2.content.behavior.slide.client;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v1.network.serverbound.slide.ServerboundCancelSlidePacket;
import mod.arcomit.parkour.v1.network.serverbound.slide.ServerboundUseSlidePacket;
import mod.arcomit.parkour.v2.content.client.NsKeyBindings;
import mod.arcomit.parkour.v2.content.client.NsKeyMapping;
import mod.arcomit.parkour.v2.content.client.event.InputJustPressedEvent;
import mod.arcomit.parkour.v2.core.context.GroundMovementData;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v2.content.behavior.slide.SlideLogic;
import mod.arcomit.parkour.v2.content.behavior.slide.SlideState;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class SlideClientHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void tryUseSlideOnInput(InputJustPressedEvent event) {
		NsKeyMapping key = event.getKeyMapping();
		if (key != NsKeyBindings.SLIDE_KEY) return;

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;

		MovementStateContext context = MovementStateContext.get(player);
		GroundMovementData groundData = context.getGroundData();
		if (SlideLogic.cannotStartSlide(player, groundData)) {
			return;
		}

		Input playerInput = player.input;
		float forwardImpulse = playerInput.forwardImpulse;
		float leftImpulse = playerInput.leftImpulse;
		boolean inAirAndSlideBackwards = !player.onGround() && forwardImpulse < 0;
		if (inAirAndSlideBackwards) {
			return;
		}
		boolean noMovementInput = forwardImpulse == 0 && leftImpulse == 0;
		if (noMovementInput) {
			return;
		}

		SlideLogic.performSlide(player, groundData, forwardImpulse, leftImpulse);
		PacketDistributor.sendToServer(new ServerboundUseSlidePacket(forwardImpulse, leftImpulse));
	}

	@SubscribeEvent
	public static void disableJumpWhileSliding(MovementInputUpdateEvent event) {
		Player player = event.getEntity();
		ParkourStateMachine stateMachine = ParkourStateMachine.get(player);
		if (stateMachine.getCurrentState() == SlideState.INSTANCE){
			event.getInput().jumping = false;
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void cancelSlideOnDownPressed(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer player)) {
			return;
		}

		ParkourStateMachine stateMachine = ParkourStateMachine.get(player);
		if (stateMachine.getCurrentState() != SlideState.INSTANCE) {
			return;
		}

		boolean downKeyPressed = player.input.down;
		if (downKeyPressed) {
			GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
			SlideLogic.cancelSlide(groundData);
			PacketDistributor.sendToServer(new ServerboundCancelSlidePacket());
		}
	}

}
