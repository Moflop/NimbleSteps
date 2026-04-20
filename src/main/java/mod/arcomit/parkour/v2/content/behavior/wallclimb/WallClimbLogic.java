package mod.arcomit.parkour.v2.content.behavior.wallclimb;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.sensor.AbstractBoxSensor;
import mod.arcomit.parkour.v2.core.sensor.SensorManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * 爬墙核心逻辑
 *
 * @author Arcomit
 */
public class WallClimbLogic {
	private static final double ZERO_THRESHOLD = 1.0E-7;
	private static final double WALL_ADHESION_FORCE = 0.1;

	private static final float SOUND_DISTANCE_MULTIPLIER = 1.0F;
	private static final float SOUND_VOLUME_MULTIPLIER = 0.15F;
	private static final double HEAD_BOX_MIN_HEIGHT_RATIO = 0.85;

	/**
	 * 执行爬墙时的物理运动与音效
	 */
	public static void useWallClimbMovement(Player player) {
		// 垂直向上爬升
		player.setDeltaMovement(0, ServerConfig.wallClimbSpeed, 0);

		// 墙面吸附
		Direction facing = player.getDirection();
		Vec3 facingVec = Vec3.atLowerCornerOf(facing.getNormal());
		Vec3 adhesionForce = facingVec.scale(WALL_ADHESION_FORCE);
		player.move(MoverType.PLAYER, adhesionForce);

		player.resetFallDistance();
		playWallClimbSound(player, facing);
	}

	/**
	 * 检查玩家正前方是否满足爬墙的碰撞条件
	 */
	public static boolean checkWallCollision(Player player) {
		SensorManager sensorManager = SensorManager.get(player);
		if (sensorManager == null) return false;

		Direction facing = player.getDirection();
		AbstractBoxSensor headSensor = sensorManager.getSensor("head_wall_" + facing.getName());
		AbstractBoxSensor feetSensor = sensorManager.getSensor("feet_wall_" + facing.getName());

		return headSensor != null && feetSensor != null
			&& headSensor.isColliding(player)
			&& feetSensor.isColliding(player);
	}

	/**
	 * 播放墙面材质的脚步音效
	 */
	private static void playWallClimbSound(Player player, Direction wallDirection) {
		double deltaX = player.getX() - player.xo;
		double deltaY = player.getY() - player.yo;
		double deltaZ = player.getZ() - player.zo;
		double actualDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

		if (actualDistance < ZERO_THRESHOLD) {
			return;
		}

		player.moveDist += (float) actualDistance * SOUND_DISTANCE_MULTIPLIER;

		if (player.moveDist <= player.nextStep) {
			return;
		}
		player.nextStep = player.moveDist + 1.0F;

		BlockPos playerPos = BlockPos.containing(player.getX(), player.getY() + player.getBbHeight() * HEAD_BOX_MIN_HEIGHT_RATIO, player.getZ());
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
			player,
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