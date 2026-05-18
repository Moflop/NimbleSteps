package mod.arcomit.parkour.content.behavior.wallclimb.server;

import mod.arcomit.parkour.core.context.WallData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-18
 */
public class ServerWallClimbSound {
	private static final double ZERO_THRESHOLD = 1.0E-7;
	private static final float SOUND_DISTANCE_MULTIPLIER = 1.2F;
	private static final float SOUND_VOLUME_MULTIPLIER = 0.15F;

	public static void playSound(Player player, WallData wallData) {
		Level level = player.level();
		// 必须在服务端执行
		if (level.isClientSide()) {
			return;
		}
		Direction wallDirection = wallData.getWallClimbCollisionDir();
		Vec3 velocity = player.getDeltaMovement();
		// 使用速度向量的长度作为移动距离
		float distanceMovedThisTick = (float) velocity.length();

		if (distanceMovedThisTick < ZERO_THRESHOLD) {
			return;
		}

		player.moveDist += distanceMovedThisTick * SOUND_DISTANCE_MULTIPLIER;

		if (player.moveDist <= player.nextStep) {
			return;
		}
		player.nextStep = player.moveDist + 1.0F;

		BlockPos playerPos = BlockPos.containing(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
		BlockPos wallPos = playerPos.relative(wallDirection);
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
