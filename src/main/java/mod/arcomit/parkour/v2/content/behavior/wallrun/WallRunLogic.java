package mod.arcomit.parkour.v2.content.behavior.wallrun;

import mod.arcomit.parkour.v1.utils.DirectionUtils;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.sensor.AbstractBoxSensor;
import mod.arcomit.parkour.v2.core.sensor.SensorManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

/**
 * 墙跑核心逻辑
 *
 * @author Arcomit
 */
public class WallRunLogic {
	private static final double ZERO_THRESHOLD = 1.0E-7;
	private static final double VANILLA_SPRINT_SPEED_MULTIPLIER = 2.15; // 原版疾跑的速度倍率
	private static final double WALL_ADHESION_FORCE = 0.1; // 墙面吸附力
	private static final float SOUND_DISTANCE_MULTIPLIER = 0.6F;
	private static final float SOUND_VOLUME_MULTIPLIER = 0.15F;

	/**
	 * 执行墙跑时的物理运动与速度修改
	 */
	public static void useWallRunMovement(Player player, ParkourContext context) {
		Direction facing = player.getDirection();
		Vec3 runDirection = new Vec3(facing.getStepX(), 0, facing.getStepZ()).normalize();

		double attributeSpeed = player.getAttributeValue(Attributes.MOVEMENT_SPEED);
		double targetSpeed = attributeSpeed * VANILLA_SPRINT_SPEED_MULTIPLIER;

		Direction wallDirection = findAvailableWallDirection(player);
		if (wallDirection != null) {
			player.setDeltaMovement(runDirection.scale(targetSpeed));

			// 墙面吸附
			Vec3 wallNormal = new Vec3(wallDirection.getStepX(), 0, wallDirection.getStepZ());
			Vec3 adhesionForce = wallNormal.scale(WALL_ADHESION_FORCE);

			boolean wasOnGround = player.onGround();
			player.move(MoverType.PLAYER, adhesionForce);
			if (wasOnGround) {
				// 恢复 onGround 状态，防止由于水平贴墙导致的意外状态结束
				player.setOnGround(true);
			}

			// 重置跌落伤害
			player.resetFallDistance();

			// 播放音效
			playWallRunSound(player, wallDirection);
		}
	}

	private static void playWallRunSound(Player player, Direction wallDirection) {
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

	/**
	 * 利用 SensorManager 获取玩家可进行墙跑的墙壁方向 (左侧或右侧)
	 */
	public static Direction findAvailableWallDirection(Player player) {
		SensorManager sensorManager = SensorManager.get(player);
		if (sensorManager == null) return null;

		Direction facing = player.getDirection();
		Direction right = facing.getClockWise();
		Direction left = facing.getCounterClockWise();

		ArrayList<Direction> collisionDirections = new ArrayList<>(2);

		// 检查右侧
		if (checkWallCollision(player, sensorManager, right)) {
			collisionDirections.add(right);
		}

		// 检查左侧
		if (checkWallCollision(player, sensorManager, left)) {
			collisionDirections.add(left);
		}

		return DirectionUtils.getClosestDirection(player, collisionDirections);
	}

	/**
	 * 辅助方法：检测特定方向的墙壁碰撞
	 */
	private static boolean checkWallCollision(Player player, SensorManager sensorManager, Direction dir) {
		// 这里根据 v2 Sensor 命名规则获取
		AbstractBoxSensor headSensor = sensorManager.getSensor("head_wall_" + dir.getName());
		AbstractBoxSensor feetSensor = sensorManager.getSensor("feet_wall_" + dir.getName());

		return headSensor != null && feetSensor != null
			&& headSensor.isColliding(player)
			&& feetSensor.isColliding(player);
	}
}