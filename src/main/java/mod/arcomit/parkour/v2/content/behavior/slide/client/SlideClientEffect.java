package mod.arcomit.parkour.v2.content.behavior.slide.client;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v2.content.init.PkSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-22
 */
@OnlyIn(Dist.CLIENT)
public class SlideClientEffect {
	private static final float SLIDE_SOUND_VOLUME = 1.0f;
	private static final float SLIDE_SOUND_PITCH = 1.0f;

	public static void playSound(Player player) {
		Minecraft.getInstance().getSoundManager().play(
			new EntityBoundSoundInstance(
				PkSounds.SLIDE.get(),
				SoundSource.PLAYERS,
				SLIDE_SOUND_VOLUME,
				SLIDE_SOUND_PITCH,
				player,
				player.getRandom().nextLong()
			)
		);
	}

	public static void applyPhysicsAndSendPosition(Player player) {
		if (!(player instanceof LocalPlayer localPlayer)) return;
		Input playerInput = localPlayer.input;
		float forwardImpulse = playerInput.forwardImpulse;
		float leftImpulse = playerInput.leftImpulse;

		float yRotRad = localPlayer.getYRot() * Mth.DEG_TO_RAD;
		float sin = Mth.sin(yRotRad);
		float cos = Mth.cos(yRotRad);

		double motionX = leftImpulse * cos - forwardImpulse * sin;
		double motionZ = forwardImpulse * cos + leftImpulse * sin;

		if (!localPlayer.onGround()) {
			if (ServerConfig.enableTapStrafing) {
				double targetYRot = Math.toDegrees(Math.atan2(-motionX, motionZ));
				float currentYRot = localPlayer.getYRot();
				float diff = Mth.wrapDegrees((float)targetYRot - currentYRot);

				localPlayer.setYRot(currentYRot + diff);
				localPlayer.yRotO = localPlayer.getYRot();
			}
		}

		Vec3 motion = new Vec3(motionX, 0, motionZ).normalize().scale(ServerConfig.slideBoostSpeed);
		localPlayer.setDeltaMovement(localPlayer.getDeltaMovement().add(motion));
		localPlayer.sendPosition();
	}
}
