package mod.arcomit.parkour.v2.content.behavior.wallrun;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.CollisionUtils;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.content.init.PkTags;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.context.WallData;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
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
	private boolean isGracePeriodOver(LocalPlayer player) {
		ParkourContext context = ParkourContext.get(player);
		return !player.input.jumping && context.wallData().getJumpReleaseGraceTicks() <= 0;
	}

	public WallRunState() {
		registerTransitions(
			// 服务端和客户端共同Tick：如果不满足条件则退出状态
			IParkourStateTransition.onTick(
				PkParkourStates.DEFAULT::get,
				player -> !this.isValid(player)
			),
			// 仅客户端检测：如果没有向前移动输入或宽限期结束，立即结束
			IParkourStateTransition.onLocalTick(
				PkParkourStates.DEFAULT::get,
				player -> player.input.forwardImpulse <= ZERO_THRESHOLD || this.isGracePeriodOver(player)
			)
		);
	}

	@Override
	public void onEnter(Player player, ParkourContext context) {
		WallData wallData = context.wallData();
		wallData.setWallRunCount(wallData.getWallRunCount() + 1);

		if (player instanceof LocalPlayer localPlayer) {
			wallData.setJumpReleaseGraceTicks(WALL_RUN_GRACE_PERIOD);
			localPlayer.sendPosition(); // 防止服务端位置没及时同步导致贴墙检测失效状态回拉
		}
	}

	@Override
	public void onTick(Player player) {
		super.onTick(player);
		ParkourContext context = ParkourContext.get(player);

		if (!(player instanceof RemotePlayer)) {
			// 施加物理运动影响与音效
			WallRunLogic.useWallRunMovement(player, context);
		}

		// 锁定身体朝向，使其不跟随视线移动
		// 强制让身体始终朝向玩家真实的移动方向（与墙壁平行）
		float targetYaw = player.getDirection().toYRot();
		player.setYBodyRot(targetYaw);
		player.yBodyRotO = targetYaw;
	}

	@Override
	public int generateVariant(Player player) {
		// 用 Variant ID 来传递墙在左边还是右边 (0 = 左侧, 1 = 右侧)
		Direction wallDir = WallRunLogic.findAvailableWallDirection(player);
		if (wallDir != null) {
			Direction facing = player.getDirection();
			return wallDir == facing.getCounterClockWise() ? 0 : 1;
		}
		return 0;
	}

	@Override
	public void onExit(Player player, ParkourContext context) {
		WallData wallData = context.wallData();
		if (player instanceof LocalPlayer) {
			wallData.setJumpReleaseGraceTicks(0);
		}
	}

	@Override
	public void onLocalPlayerTick(LocalPlayer player, ParkourContext context) {
		WallData wallData = context.wallData();
		if (player.input.jumping) {
			// 如果一直按着跳跃键，重置宽限期
			wallData.setJumpReleaseGraceTicks(WALL_RUN_GRACE_PERIOD);
		}else {
			int grace = wallData.getJumpReleaseGraceTicks();
			if (grace > 0) {
				wallData.setJumpReleaseGraceTicks(grace - 1);
			}
		}
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	/**
	 * 验证玩家当前环境是否满足维持墙跑的条件
	 */
	public static boolean isBaseValid(Player player) {
		boolean noFrontColliding = !CollisionUtils.isEntityCollidingWithBlockInDirection(
			player, player.getDirection(), PkTags.Blocks.COMMON_IGNORED_BLOCKS);

		return ServerConfig.enableWallRun
			&& player.isSprinting()
			&& !player.onGround()
			&& WallRunLogic.findAvailableWallDirection(player) != null // 使用 v2 Sensor 检测左右侧
			&& noFrontColliding // 确保正前方没有撞墙
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	@Override
	public boolean canEnter(Player player) {
		ParkourContext context = ParkourContext.get(player);
		WallData wallData = context.wallData();

		boolean isFallingOnAir = player.getDeltaMovement().y() < 0 && !player.onGround();
		boolean hasRemainingWallRunCount = wallData.getWallRunCount() < ServerConfig.maxWallRunCount;

		return isBaseValid(player)
			&& isFallingOnAir
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& hasRemainingWallRunCount;
	}

	@Override
	public boolean isValid(Player player) {
		StateData stateData = ParkourContext.get(player).stateData();
		return isBaseValid(player) && stateData.getTicksInState() < ServerConfig.wallRunDuration;
	}
}