package mod.arcomit.parkour.content.behavior.wallslide;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.content.init.ParkourTags;
import mod.arcomit.parkour.utils.CollisionUtils;
import mod.arcomit.parkour.utils.PlayerStateUtils;
import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.context.WallData;
import mod.arcomit.parkour.core.proxy.ParkourProxies;
import mod.arcomit.parkour.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

/**
 * 滑墙状态
 *
 * @author Arcomit
 */
public class WallSlideState extends AbstractParkourState {

	/**
	 * 判断宽限期是否结束
	 */
	private boolean isGracePeriodOver(ParkourContext context) {
		return context.jumpData().getJumpReleaseGraceTicks() <= 0;
	}


	public WallSlideState() {
		registerTransitions(
			IParkourStateTransition.onLocalTick(
				ParkourStates.DEFAULT::get,
				(player, context) -> this.isGracePeriodOver(context)
			)
		);
	}

	@Override
	public void onClientEnter(Player player, ParkourContext context) {
		ParkourProxies.PLAYER_SERVICES_PROXY.sendPosition(player);
	}

	@Override
	public void onSimulationTick(Player player, ParkourContext context) {
		WallData wallData = context.wallData();
		// 施加物理运动影响
		WallSlideLogic.applySlowdownAndTrySwitchCollisionDirection(player, wallData);
	}

	@Override
	public void onExit(Player player, ParkourContext context) {
		super.onExit(player, context);
		WallData wallData = context.wallData();
		wallData.resetWallSlideCollisionDir();
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	/**
	 * 验证玩家当前环境是否满足滑墙条件
	 */
	public static boolean isBaseValid(Player player, ParkourContext context) {
		// 快速失败：基础状态不满足墙跑条件
		if (!ParkourConfig.enableWallSlide || player.onGround() || player.onClimbable()
			|| player.isInWater() || player.isInLava() ||
			!PlayerStateUtils.isAbleToBehavior(player)) {
			return false;
		}

		// 检测附近是否有可以依附的墙
		WallData wallData = context.wallData();
		if (WallSlideLogic.findAvailableWallDirection(player, wallData) == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canEnter(Player player, ParkourContext context) {
		return isBaseValid(player, context);
	}

	@Override
	public boolean isValid(Player player, ParkourContext context) {
		return isBaseValid(player, context);
	}
}