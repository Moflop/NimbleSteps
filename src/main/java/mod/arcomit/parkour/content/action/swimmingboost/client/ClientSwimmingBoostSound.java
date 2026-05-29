package mod.arcomit.parkour.content.action.swimmingboost.client;

import mod.arcomit.parkour.content.action.swimmingboost.SwimmingBoostSound;
import mod.arcomit.parkour.core.proxy.ParkourProxies;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-29
 */
public class ClientSwimmingBoostSound {

	public static void play(Player player) {
		ParkourProxies.SOUND_PROXY.playEntityBoundSound(
			SoundEvents.AMBIENT_UNDERWATER_ENTER,
			SoundSource.PLAYERS,
			SwimmingBoostSound.SWIMMING_BOOST_SOUND_VOLUME,
			SwimmingBoostSound.SWIMMING_BOOST_SOUND_PITCH,
			player,
			player.getRandom().nextLong()
		);
	}
}
