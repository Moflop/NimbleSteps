package mod.arcomit.parkour.v2.content.behavior.wallrun.client;

import mod.arcomit.parkour.v2.core.context.WallData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-10
 */
public class ClientWallRunLogic {
	private static final double ZERO_THRESHOLD = 1.0E-7;
	private static final float SOUND_DISTANCE_MULTIPLIER = 0.6F;
	private static final float SOUND_VOLUME_MULTIPLIER = 0.15F;

	public static void playWallRunSound(Player player, WallData wallData) {
		Direction wallDirection = Direction.from3DDataValue(wallData.getWallRunCollisionDir3DData());
		double deltaX = player.getX() - player.xo;
		double deltaY = player.getY() - player.yo;
		double deltaZ = player.getZ() - player.zo;
		double actualDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

		if (actualDistance < ZERO_THRESHOLD) {
			return;
		}

		float distanceTraveled = (float) actualDistance * SOUND_DISTANCE_MULTIPLIER;
		player.moveDist += distanceTraveled;

		if (player.moveDist <= player.nextStep) {
			return;
		}
		player.nextStep = player.moveDist + 1.0F;

		BlockPos playerPos = player.blockPosition();
		BlockPos wallPos = playerPos.relative(wallDirection);
		Level level = player.level();
		BlockState blockState = level.getBlockState(wallPos);

		if (blockState.isAir()) {
			return;
		}

		SoundType soundType = blockState.getSoundType(level, wallPos, player);
		float volume = soundType.getVolume() * SOUND_VOLUME_MULTIPLIER;
		float pitch = soundType.getPitch();

		level.playSound(
			null,
			wallPos.getX(),
			wallPos.getY(),
			wallPos.getZ(),
			soundType.getStepSound(),
			SoundSource.PLAYERS,
			volume,
			pitch
		);
	}
}
