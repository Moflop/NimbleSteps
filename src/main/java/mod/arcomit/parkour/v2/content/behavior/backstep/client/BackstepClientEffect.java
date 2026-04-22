package mod.arcomit.parkour.v2.content.behavior.backstep.client;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v2.content.init.PkSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-22
 */
public class BackstepClientEffect {
	private static final float BACKSTEPS_SOUND_VOLUME = 0.6f;
	private static final float BACKSTEPS_SOUND_PITCH = 1.4f;

	public static void playSound(Player player) {
		Minecraft.getInstance().getSoundManager().play(
			new EntityBoundSoundInstance(
				SoundEvents.WIND_CHARGE_THROW,
				SoundSource.PLAYERS,
				BACKSTEPS_SOUND_VOLUME,
				BACKSTEPS_SOUND_PITCH,
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

		Vec3 motion = new Vec3(motionX, 0, motionZ).normalize().scale(ServerConfig.slideBoostSpeed);
		localPlayer.setDeltaMovement(localPlayer.getDeltaMovement().add(motion));
		localPlayer.sendPosition();
	}
}
