package mod.arcomit.parkour.content.behavior.slide.client;

import mod.arcomit.parkour.content.init.ParkourSounds;
import mod.arcomit.parkour.core.proxy.ParkourProxies;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-29
 */
public class ClientSlideSound {
	private static final float SLIDE_SOUND_VOLUME = 1.0f;
	private static final float SLIDE_SOUND_PITCH = 1.0f;

	public static void play(Player player) {
		ParkourProxies.SOUND_PROXY.playEntityBoundSound(
			ParkourSounds.SLIDE.get(),
			SoundSource.PLAYERS,
			SLIDE_SOUND_VOLUME,
			SLIDE_SOUND_PITCH,
			player,
			player.getRandom().nextLong()
		);
	}
}
