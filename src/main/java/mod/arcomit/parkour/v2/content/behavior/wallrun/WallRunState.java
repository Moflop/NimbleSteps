package mod.arcomit.parkour.v2.content.behavior.wallrun;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.v1.utils.CollisionUtils;
import mod.arcomit.parkour.v1.utils.PlayerDirectionUtils;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.behavior.wallrun.client.ClientWallRunLogic;
import mod.arcomit.parkour.v2.content.init.ParkourStates;
import mod.arcomit.parkour.v2.content.init.ParkourTags;
import mod.arcomit.parkour.v2.core.context.JumpData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.context.WallData;
import mod.arcomit.parkour.v2.core.proxy.ParkourProxies;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

/**
 * 墙跑状态
 * * @author Arcomit
 */
public class WallRunState extends AbstractParkourState {
	public static final int WALL_RUN_GRACE_PERIOD = 6; // 松开跳跃键后最大墙跑持续时间
	public static final int MAX_TICKS_SINCE_JUMP = 15; // 在跳跃后的15个刻之内才能开始墙跑
	public static final double ZERO_THRESHOLD = 1.0E-7;

	/**
	 * 判断宽限期是否结束
	 */
	private boolean isGracePeriodOver(ParkourContext context) {
		return context.jumpData().getJumpReleaseGraceTicks() <= 0;
	}

	public WallRunState() {
		registerTransitions(
			// 仅客户端检测：如果没有向前移动输入或宽限期结束，立即结束
			IParkourStateTransition.onLocalTick(
				ParkourStates.DEFAULT::get,
				(player, context) -> ParkourProxies.INPUT_PROXY.getForwardImpulse(player) <= ZERO_THRESHOLD
					|| this.isGracePeriodOver(context)
			)
		);
	}

	@Override
	public void onEnter(Player player, ParkourContext context) {
		super.onEnter(player, context);
		WallData wallData = context.wallData();
		JumpData jumpData = context.jumpData();
		WallRunLogic.setCollisionDirAndFixedMovementDir(player, wallData);
		jumpData.resetLastForwardWallJumpDir3DData();// 重置顺着运动方向跳的Dir，以实现墙跑刷新跳跃次数。
	}

	@Override
	public void onClientEnter(Player player, ParkourContext context) {
		ParkourProxies.PLAYER_SERVICES_PROXY.sendPosition(player);
	}

	@Override
	public void onSimulationTick(Player player, ParkourContext context) {
		WallData wallData = context.wallData();
		// 施加物理运动效果
		WallRunLogic.useWallRunMovement(player, wallData);
	}

	@Override
	public void onClientTick(Player player, ParkourContext context) {
		super.onClientTick(player, context);
		WallData wallData = context.wallData();
		ClientWallRunLogic.playWallRunSound(player, wallData);
	}

	@Override
	public int generateVariant(Player player) {
		// 用 Variant ID 来传递墙在左边还是右边 (0 = 左侧, 1 = 右侧)
		Direction wallDir = WallRunLogic.findFirstWallCollisionDirection(player);
		if (wallDir != null) {
			Direction facing = player.getDirection();
			return wallDir == facing.getCounterClockWise() ? 0 : 1;
		}
		return 0;
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	/**
	 * 验证玩家当前环境是否满足维持墙跑的条件
	 */
	public static boolean isBaseValid(Player player) {
		// 快速失败：基础状态不满足墙跑条件
		if (!ParkourConfig.enableWallRun || !player.isSprinting() || player.onGround() ||
			player.onClimbable() || player.isInWater() || player.isInLava() ||
			!PlayerStateUtils.isAbleToBehavior(player)) {
			return false;
		}

		// 确保正前方没有撞墙
		if (CollisionUtils.isEntityCollidingWithBlockInDirection(
			player, player.getDirection(), ParkourTags.Blocks.COMMON_IGNORED_BLOCKS)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canEnter(Player player, ParkourContext context) {
		if (!isBaseValid(player)) {
			return false;
		}
		Direction availableWallDir = WallRunLogic.findFirstWallCollisionDirection(player);
		if (availableWallDir == null) {
			return false;
		}
		WallData wallData = context.wallData();
		if (availableWallDir.get3DDataValue() == wallData.getWallRunCollisionDir3DData()) {
			return false;
		}
		boolean isFalling = player.getDeltaMovement().y() < 0;
		if (!isFalling) {
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
		if (stateData.getTicksInState() >= ParkourConfig.wallRunDuration) {
			return false;
		}
		if (!WallRunLogic.wallCollisionIsValid(player, context)) {
			return false;
		}
		WallData wallData = context.wallData();
		Direction movementDir = Direction.from3DDataValue(wallData.getWallRunMovementDir3DData());
		return PlayerDirectionUtils.isLookingInDirection_Horizontal(player, movementDir, 90);
	}
}