package mod.arcomit.parkour.content.behavior.slide.client;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.content.init.ParkourSounds;
import mod.arcomit.parkour.core.proxy.ParkourProxies;
import mod.arcomit.parkour.core.proxy.api.IInputProxy;
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
public class ClientSlideLogic {
	private static final float SLIDE_SOUND_VOLUME = 1.0f;
	private static final float SLIDE_SOUND_PITCH = 1.0f;

	public static void playSound(Player player) {
		ParkourProxies.SOUND_PROXY.playEntityBoundSound(
			ParkourSounds.SLIDE.get(),
			SoundSource.PLAYERS,
			SLIDE_SOUND_VOLUME,
			SLIDE_SOUND_PITCH,
			player,
			player.getRandom().nextLong()
		);
	}

	public static void applyPhysicsAndSendPosition(Player player) {
		if (!player.isLocalPlayer()) return;
		IInputProxy input = ParkourProxies.INPUT_PROXY;
		float forwardImpulse = input.getForwardImpulse(player);
		float leftImpulse = input.getLeftImpulse(player);

		float yRotRad = player.getYRot() * Mth.DEG_TO_RAD;
		float sin = Mth.sin(yRotRad);
		float cos = Mth.cos(yRotRad);

		double motionX = leftImpulse * cos - forwardImpulse * sin;
		double motionZ = forwardImpulse * cos + leftImpulse * sin;

		if (!player.onGround()) {
			if (ParkourConfig.enableTapStrafing) {
				double targetYRot = Math.toDegrees(Math.atan2(-motionX, motionZ));
				float currentYRot = player.getYRot();
				float diff = Mth.wrapDegrees((float)targetYRot - currentYRot);

				player.setYRot(currentYRot + diff);
				player.yRotO = player.getYRot();
			}
		}

		Vec3 motion = new Vec3(motionX, 0, motionZ).normalize().scale(ParkourConfig.slideBoostSpeed);
		player.setDeltaMovement(player.getDeltaMovement().add(motion));
		ParkourProxies.PLAYER_SERVICES_PROXY.sendPosition(player);
	}
}
