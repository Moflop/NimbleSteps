package mod.arcomit.parkour.v2.content.behavior.wallslide;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.WallData;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

/**
 * 滑墙状态
 *
 * @author Arcomit
 */
public class WallSlideState extends AbstractParkourState {
	public static final int WALL_SLIDE_GRACE_PERIOD = 6; // 松开跳跃键后的吸附宽限期

	/**
	 * 判断宽限期是否结束
	 */
	private boolean isGracePeriodOver(LocalPlayer player) {
		ParkourContext context = ParkourContext.get(player);
		return !player.input.jumping && context.wallData().getWallSlideJumpReleaseGraceTicks() <= 0;
	}

	public WallSlideState() {
		registerTransitions(
			IParkourStateTransition.onTick(
				PkParkourStates.DEFAULT::get,
				player -> !this.isValid(player)
			),
			IParkourStateTransition.onLocalTick(
				PkParkourStates.DEFAULT::get,
				this::isGracePeriodOver
			)
		);
	}

	@Override
	public void onEnter(Player player) {
		WallData wallData = ParkourContext.get(player).wallData();

		if (player instanceof LocalPlayer localPlayer) {
			wallData.setWallSlideJumpReleaseGraceTicks(WALL_SLIDE_GRACE_PERIOD);
			localPlayer.sendPosition();// 防止服务端位置没及时同步导致贴墙检测失效状态回拉
		}
	}

	@Override
	public void onTick(Player player) {
		super.onTick(player);
		if (player instanceof RemotePlayer) return;
		ParkourContext context = ParkourContext.get(player);
		// 施加物理运动影响
		WallSlideLogic.useWallSlideMovement(player, context);
	}


	@Override
	public void onExit(Player player) {
		WallData wallData = ParkourContext.get(player).wallData();

		wallData.setWallSlideDirection(-1);
		if (player instanceof LocalPlayer) {
			wallData.setWallSlideJumpReleaseGraceTicks(0);
		}
	}

	@Override
	public void onLocalPlayerTick(LocalPlayer player) {
		WallData wallData = ParkourContext.get(player).wallData();
		if (player.input.jumping) {
			// 如果一直按着跳跃键，重置宽限期
			wallData.setWallSlideJumpReleaseGraceTicks(WALL_SLIDE_GRACE_PERIOD);
		}else {
			int grace = wallData.getWallSlideJumpReleaseGraceTicks();
			if (grace > 0) {
				wallData.setWallSlideJumpReleaseGraceTicks(grace - 1);
			}
		}
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	/**
	 * 验证玩家当前环境是否满足滑墙条件
	 */
	public static boolean isBaseValid(Player player) {
		boolean isFallingOnAir = player.getDeltaMovement().y() < 0 && !player.onGround();

		return ServerConfig.enableWallSlide
			&& isFallingOnAir
    			&& WallSlideLogic.findAvailableWallDirection(player) != null // 使用 v2 Sensor 检测
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	@Override
	public boolean canEnter(Player player) {
		return isBaseValid(player);
	}

	@Override
	public boolean isValid(Player player) {
		return isBaseValid(player);
	}
}