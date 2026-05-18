package mod.arcomit.parkour.content.behavior.backstep.client;

import mod.arcomit.parkour.core.proxy.ParkourProxies;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-17
 */
public class ClientBackstepSound {
	private static final float BACKSTEPS_SOUND_VOLUME = 0.6f;
	private static final float BACKSTEPS_SOUND_PITCH = 1.4f;

	public static void playSound(Player player) {
		ParkourProxies.SOUND_PROXY.playEntityBoundSound(
			SoundEvents.BREEZE_SLIDE,
			SoundSource.PLAYERS,
			BACKSTEPS_SOUND_VOLUME,
			BACKSTEPS_SOUND_PITCH,
			player,
			player.getRandom().nextLong()
		);
	}
}
