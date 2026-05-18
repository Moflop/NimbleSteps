package mod.arcomit.parkour.content.behavior.wallclimb;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.content.behavior.wallclimb.server.ServerWallClimbSound;
import mod.arcomit.parkour.content.behavior.wallrun.server.ServerWallRunSound;
import mod.arcomit.parkour.core.context.JumpData;
import mod.arcomit.parkour.core.context.WallData;
import mod.arcomit.parkour.utils.PlayerStateUtils;
import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.context.StateData;
import mod.arcomit.parkour.core.proxy.ParkourProxies;
import mod.arcomit.parkour.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.core.statemachine.state.IParkourStateTransition;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 爬墙状态
 *
 * @author Arcomit
 */
public class WallClimbState extends AbstractParkourState {
	public static final int MAX_TICKS_SINCE_JUMP = 15;
	public static final double ZERO_THRESHOLD = 1.0E-7;

	public WallClimbState() {
		registerTransitions(
			// 客户端输入中断判断（松开跳跃或停止前进）
			IParkourStateTransition.onLocalTick(
				ParkourStates.DEFAULT::get,
				(player, context) -> ParkourProxies.INPUT_PROXY.getForwardImpulse(player) <= ZERO_THRESHOLD
					|| !ParkourProxies.INPUT_PROXY.getJumping(player)
			)
		);
	}

	@Override
	public void onEnter(Player player, ParkourContext context) {
		super.onEnter(player, context);
		WallData wallData = context.wallData();
		JumpData jumpData = context.jumpData();
		WallClimbLogic.setCollisionDir(player, wallData);
		jumpData.resetLastUpWallJumpDir();// 重置向上方向跳的Dir，以实现爬墙刷新跳跃次数。
	}

	@Override
	public void onClientEnter(Player player, ParkourContext context) {
		ParkourProxies.PLAYER_SERVICES_PROXY.sendPosition(player);
	}

	@Override
	public void onSimulationTick(Player player, ParkourContext context) {
		WallClimbLogic.useWallClimbMovement(player);
	}

	@Override
	public void onServerTick(Player player, ParkourContext context) {
		WallData wallData = context.wallData();
		ServerWallClimbSound.playSound(player, wallData);
	}

	@Override
	public void onSimulationExit(Player player, ParkourContext context) {
		player.setDeltaMovement(Vec3.ZERO);// 避免退出状态时有一瞬间墙跳不了影响手感
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	/**
	 * 验证玩家当前环境是否满足维持爬墙的条件
	 */
	public static boolean isBaseValid(Player player) {
		// 快速失败：基础状态不满足墙跑条件
		if (!ParkourConfig.enableWallClimb || !player.isSprinting() || player.onGround() ||
			player.onClimbable() || player.isInWater() || player.isInLava() ||
			!PlayerStateUtils.isAbleToBehavior(player)) {
			return false;
		}

		// 检查玩家前方是否有墙
		if (!WallClimbLogic.checkWallCollision(player)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canEnter(Player player, ParkourContext context) {
		if (!isBaseValid(player)) {
			return false;
		}
		boolean notFalling = player.getDeltaMovement().y() >= 0;
		if (notFalling) {
			return false;
		}
		Direction availableWallDir = player.getDirection();
		WallData wallData = context.wallData();
		if (availableWallDir == wallData.getWallClimbCollisionDir()) {
			return false;
		}
		return !PlayerStateUtils.fallWillTakeDamage(player);
	}

	@Override
	public boolean isValid(Player player, ParkourContext context) {
		if (!isBaseValid(player)) {
			return false;
		}
		StateData stateData = context.stateData();
		return stateData.getTicksInState() < ParkourConfig.wallClimbDuration;
	}
}