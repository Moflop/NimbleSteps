package mod.arcomit.parkour.content.action.walljump;

import mod.arcomit.parkour.content.init.ParkourSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class WallJumpSound {
	private static final float WALL_JUMP_SOUND_VOLUME = 1.5f;
	private static final float WALL_JUMP_SOUND_PITCH = 1.0f;

	public static void playWallJumpSound(Player player) {
		Level level = player.level();
		if (!level.isClientSide) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(),
				ParkourSounds.WALL_JUMP.get(), SoundSource.PLAYERS,
				WALL_JUMP_SOUND_VOLUME, WALL_JUMP_SOUND_PITCH);
		}
	}
}
