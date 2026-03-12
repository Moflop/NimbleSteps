package mod.arcomit.parkour.v2.content.behavior.slide;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.DirectionUtils;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v2.core.context.WallMovementData;
import mod.arcomit.parkour.v2.core.sensor.AbstractBoxSensor;
import mod.arcomit.parkour.v2.core.sensor.SensorManager;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

/**
 * 滑墙逻辑类
 *
 * @author Arcomit
 */
public class WallSlideLogic {
	public static final int WALL_SLIDE_GRACE_PERIOD = 6;
	private static final double WALL_ADHESION_FORCE = 0.1;
	private static final double HORIZONTAL_SLOWDOWN = 0.8;
	private static final double VERTICAL_SLOWDOWN = 0.7;

	public static void startWallSliding(WallMovementData wallData) {
		wallData.setWallSliding(true);
	}

	public static void endWallSliding(WallMovementData wallData) {
		wallData.setWallSliding(false);
		wallData.setWallSlideJumpReleaseGraceTicks(0);
	}

	public static void useWallSlideMovement(Player player, MovementStateContext state) {
		Vec3 motion = player.getDeltaMovement();

		Direction wallDir = findAvailableWallDirection(player, state);
		if (wallDir != null) {
			Vec3 slowedMotion = motion.multiply(HORIZONTAL_SLOWDOWN, VERTICAL_SLOWDOWN, HORIZONTAL_SLOWDOWN);
			player.setDeltaMovement(slowedMotion);

			// 墙面吸附
			Vec3 wallDirVec = new Vec3(wallDir.getStepX(), 0, wallDir.getStepZ());
			Vec3 adhesionForce = wallDirVec.scale(WALL_ADHESION_FORCE);
			player.move(MoverType.PLAYER, adhesionForce);
			player.resetFallDistance();
		}
	}

	public static void updateWallSlideGracePeriod(WallMovementData wallData) {
		int ticks = wallData.getWallSlideJumpReleaseGraceTicks();
		if (ticks == 0) {
			ticks = WALL_SLIDE_GRACE_PERIOD;
		} else {
			ticks--;
		}
		wallData.setWallSlideJumpReleaseGraceTicks(ticks);

		if (ticks <= 0) {
			endWallSliding(wallData);
		}
	}

	public static boolean canStartWallSlide(Player player, MovementStateContext state) {
		ParkourStateMachine stateMachine = ParkourStateMachine.get(player);
		if (!stateMachine.isDefaultState()) {
			return false;
		}
		return isValid(player, state);
	}

	public static boolean isValid(Player player, MovementStateContext state) {
		boolean isFallingOnAir = player.getDeltaMovement().y() < 0 && !player.onGround();
		return ServerConfig.enableWallSlide
			&& isFallingOnAir
			&& findAvailableWallDirection(player, state) != null // 使用 Sensor 进行方向判断
			&& state.getGroundData().getSlideDuration() <= 0
			&& !state.getWallData().isWallRunning()
			&& !state.getWallData().isWallClimbing()
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	/**
	 * 依靠 SensorManager 查找当前哪一侧的墙壁是合法的
	 */
	private static Direction findAvailableWallDirection(Player player, MovementStateContext state) {
		SensorManager sensorManager = state.getSensorManager();
		ArrayList<Direction> collisionDirections = new ArrayList<>(4);

		for (Direction dir : Direction.Plane.HORIZONTAL) {
			AbstractBoxSensor headSensor = sensorManager.get("wall_head_" + dir.getName());
			AbstractBoxSensor feetSensor = sensorManager.get("wall_feet_" + dir.getName());

			// 只有当同方向的头部和脚部都检测到墙体时，才认为该面墙有效
			if (headSensor != null && feetSensor != null
				&& headSensor.isColliding(player)
				&& feetSensor.isColliding(player)) {
				collisionDirections.add(dir);
			}
		}

		return DirectionUtils.getClosestDirection(player, collisionDirections);
	}
}