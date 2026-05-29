package mod.arcomit.parkour.content.behavior.landingroll.client;

import mod.arcomit.parkour.content.init.ParkourSounds;
import mod.arcomit.parkour.core.proxy.ParkourProxies;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-22
 */
public class ClientLandingRollSound {
	private static final float ROLL_SOUND_VOLUME = 1.0f;
	private static final float ROLL_SOUND_PITCH = 1.0f;

	public static void play(Player player) {
		ParkourProxies.SOUND_PROXY.playEntityBoundSound(
			ParkourSounds.LANDING_ROLL.get(),
			SoundSource.PLAYERS,
			ROLL_SOUND_VOLUME,
			ROLL_SOUND_PITCH,
			player,
			player.getRandom().nextLong()
		);
	}
}
