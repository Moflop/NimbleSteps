package mod.arcomit.parkour.v2.content.behavior.wallclimb;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.init.ParkourStates;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.context.WallData;
import mod.arcomit.parkour.v2.core.proxy.ParkourProxies;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
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
		WallClimbLogic.setWallClimbed(player, context);
	}

	@Override
	public void onExit(Player player, ParkourContext context) {
		player.setDeltaMovement(Vec3.ZERO);
	}

	@Override
	public void onSimulationTick(Player player, ParkourContext context) {
		WallClimbLogic.useWallClimbMovement(player);
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	/**
	 * 验证玩家当前环境是否满足维持爬墙的条件
	 */
	public static boolean isBaseValid(Player player) {
		return ParkourConfig.enableWallClimb
			&& player.isSprinting()
			&& !player.onGround()
			&& WallClimbLogic.checkWallCollision(player)
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToBehavior(player);
	}

	@Override
	public boolean canEnter(Player player, ParkourContext context) {
		boolean isFallingOnAir = player.getDeltaMovement().y() < 0 && !player.onGround();

		return isBaseValid(player)
			&& isFallingOnAir
			&& !PlayerStateUtils.fallWillTakeDamage(player);
	}

	@Override
	public boolean isValid(Player player, ParkourContext context) {
		StateData stateData = context.stateData();
		return isBaseValid(player) && stateData.getTicksInState() < ParkourConfig.wallClimbDuration;
	}
}