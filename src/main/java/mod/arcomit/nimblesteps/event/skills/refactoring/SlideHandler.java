package mod.arcomit.nimblesteps.event.skills.refactoring;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.client.NsKeyBindings;
import mod.arcomit.nimblesteps.client.NsKeyMapping;
import mod.arcomit.nimblesteps.client.event.InputJustPressedEvent;
import mod.arcomit.nimblesteps.init.NsSounds;
import mod.arcomit.nimblesteps.network.serverbound.slide.ServerboundCancelSlidePacket;
import mod.arcomit.nimblesteps.network.serverbound.slide.ServerboundUseSlidePacket;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 滑铲处理器。
 *
 * @author Arcomit
 * @since 2026-01-05
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class SlideHandler {
	private static final int SLIDE_DURATION = 10;
	private static final double STOP_SLIDING_VELOCITY_THRESHOLD = 0.1;

	private static final float SLIDE_SOUND_VOLUME = 1.0f;
	private static final float SLIDE_SOUND_PITCH = 1.0f;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void trySlideOnInput(InputJustPressedEvent event) {
		NsKeyMapping key = event.getKeyMapping();
		if (key != NsKeyBindings.SLIDE_KEY) {
			return;
		}

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (!canSlide(player, state)) {
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

		useSlide(player, state, forwardImpulse, leftImpulse);
		PacketDistributor.sendToServer(new ServerboundUseSlidePacket(forwardImpulse, leftImpulse));
	}

	@SubscribeEvent
	public static void tickSlideTimers(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		int slideCooldown = state.getSlideCooldown();
		if (slideCooldown > 0) {
			state.setSlideCooldown(slideCooldown - 1);
		}

		int currentDuration = state.getSlideDuration();
		if (currentDuration > 0) {
			int newDuration = currentDuration - 1;
			state.setSlideDuration(newDuration);
			if (newDuration == 0) {
				player.setForcedPose(null);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void checkSlideInterrupt(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer player)) {
			return;
		}

		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (!state.isSliding()) {
			return;
		}

		boolean downKeyPressed = player.input.down;
		if (downKeyPressed) {
			cancelSlide(player, state);
			PacketDistributor.sendToServer(new ServerboundCancelSlidePacket());
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void disableJumpWhileSliding(MovementInputUpdateEvent event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (state.isSliding()){
			event.getInput().jumping = false;
		}
	}

	public static void useSlide(Player player, NimbleStepsState state, float forwardImpulse, float leftImpulse) {
		if (forwardImpulse >= 0) {
			state.setSlideDuration(SLIDE_DURATION);
			player.setForcedPose(Pose.SWIMMING);
		}

		float yRotRad = player.getYRot() * Mth.DEG_TO_RAD;

		float sin = Mth.sin(yRotRad);
		float cos = Mth.cos(yRotRad);

		// 计算相对于视角的运动矢量
		double motionX = leftImpulse * cos - forwardImpulse * sin;
		double motionZ = forwardImpulse * cos + leftImpulse * sin;

		if (!player.onGround()) {
			state.resetLastWallJumpDirection();
			if (ServerConfig.enableTapStrafing) {
				double targetYRot = Math.toDegrees(Math.atan2(-motionX, motionZ));
				float currentYRot = player.getYRot();
				float diff = Mth.wrapDegrees((float)targetYRot - currentYRot);

				player.setYRot(currentYRot + diff);
				player.yRotO = player.getYRot();
			}
		}

		Vec3 motion = new Vec3(motionX, 0, motionZ).normalize()
			.scale(ServerConfig.slideBoostSpeed);
		player.setDeltaMovement(
			player.getDeltaMovement().add(motion)
		);


		Level level = player.level();
		if (level.isClientSide) {
			Minecraft.getInstance().getSoundManager().play(
				new EntityBoundSoundInstance(
					NsSounds.SLIDE.get(),
					SoundSource.PLAYERS,
					SLIDE_SOUND_VOLUME,
					SLIDE_SOUND_PITCH,
					player,
					player.getRandom().nextLong()));
		} else {
			level.playSound(
				player,
				player.getX(),
				player.getY(),
				player.getZ(),
				NsSounds.SLIDE.get(),
				SoundSource.PLAYERS,
				SLIDE_SOUND_VOLUME,
				SLIDE_SOUND_PITCH);
		}
		state.setSlideCooldown(ServerConfig.slideCooldown);
		player.resetFallDistance();
	}

	public static void cancelSlide(Player player, NimbleStepsState state) {
		state.setSlideDuration(0);
		player.setForcedPose(null);
	}

	public static boolean canSlide(Player player, NimbleStepsState state) {
		return ServerConfig.enableSlide
			&& state.getSlideCooldown() <= 0
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !state.isCrawling()
			&& !state.isWallRunning()
			&& !state.isWallSliding()
			&& !player.isCrouching()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}
