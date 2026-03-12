package mod.arcomit.parkour.v2.content.behavior.slide;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.init.NsSounds;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.core.context.GroundMovementData;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
public class SlideLogic {
	public static final int SLIDE_DURATION = 10; // 滑铲默认持续时间（以刻为单位）
	private static final float SLIDE_SOUND_VOLUME = 1.0f;
	private static final float SLIDE_SOUND_PITCH = 1.0f;

	public static void performSlide(Player player, GroundMovementData groundData, float forwardImpulse, float leftImpulse) {
		if (forwardImpulse >= 0) {
			groundData.setSlideDuration(SLIDE_DURATION);
			player.setForcedPose(Pose.SWIMMING);
		}

		float yRotRad = player.getYRot() * Mth.DEG_TO_RAD;

		float sin = Mth.sin(yRotRad);
		float cos = Mth.cos(yRotRad);

		// 计算相对于视角的运动矢量
		double motionX = leftImpulse * cos - forwardImpulse * sin;
		double motionZ = forwardImpulse * cos + leftImpulse * sin;

		if (!player.onGround()) {
			//todo: 滑铲重置墙跳？（待定）groundData.resetLastWallJumpDirection();
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
					SlideLogic.SLIDE_SOUND_VOLUME,
					SlideLogic.SLIDE_SOUND_PITCH,
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
				SlideLogic.SLIDE_SOUND_VOLUME,
				SlideLogic.SLIDE_SOUND_PITCH);
		}
		groundData.setSlideCooldown(ServerConfig.slideCooldown);
		player.resetFallDistance();
	}

	public static void cancelSlide(GroundMovementData groundData) {
		groundData.setSlideDuration(0);
	}

	public static boolean cannotStartSlide(Player player, GroundMovementData groundData) {
		ParkourStateMachine stateMachine = ParkourStateMachine.get(player);

		if (!stateMachine.isDefaultState()) {
			return true;
		}
		if (groundData.getSlideCooldown() > 0) {
			return true;
		}
		if (!isValid(player)) {
			return true;
		}
		if (player.isCrouching()) {
			return true;
		}

		return false;
	}

	public static boolean isValid(Player player) {
		return ServerConfig.enableSlide
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}
