package mod.arcomit.parkour.content.action.swimmingboost;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-29
 */
public class SwimmingBoostSound {
	public static final float SWIMMING_BOOST_SOUND_VOLUME = 0.9f;
	public static final float SWIMMING_BOOST_SOUND_PITCH = 0.8f;

	public static void play(Player player) {
		Level level = player.level();
		level.playSound(
			player,
			player.getX(),
			player.getY(),
			player.getZ(),
			SoundEvents.AMBIENT_UNDERWATER_ENTER,
			SoundSource.PLAYERS,
			SWIMMING_BOOST_SOUND_VOLUME,
			SWIMMING_BOOST_SOUND_PITCH);
	}
}
