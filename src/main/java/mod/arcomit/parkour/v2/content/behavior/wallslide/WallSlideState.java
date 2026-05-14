package mod.arcomit.parkour.v2.content.behavior.wallslide;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.init.ParkourStates;
import mod.arcomit.parkour.v2.core.context.JumpData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.WallData;
import mod.arcomit.parkour.v2.core.proxy.ParkourProxies;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLEnvironment;

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
	private boolean isGracePeriodOver(Player player, ParkourContext context) {
		return !ParkourProxies.INPUT_PROXY.getJumping(player) && context.jumpData().getJumpReleaseGraceTicks() <= 0;
	}

	public WallSlideState() {
		registerTransitions(
			IParkourStateTransition.onLocalTick(
				ParkourStates.DEFAULT::get,
				this::isGracePeriodOver
			)
		);
	}

	@Override
	public void onEnter(Player player, ParkourContext context) {
		JumpData jumpData = context.jumpData();

		if (player instanceof LocalPlayer localPlayer) {
			jumpData.setJumpReleaseGraceTicks(WALL_SLIDE_GRACE_PERIOD);
			localPlayer.sendPosition();// 防止服务端位置没及时同步导致贴墙检测失效状态回拉
		}
	}

	@Override
	public void onTick(Player player, ParkourContext context) {
		super.onTick(player, context);
		if (player instanceof RemotePlayer) return;
		// 施加物理运动影响
		WallSlideLogic.useWallSlideMovement(player, context);
	}


	@Override
	public void onExit(Player player, ParkourContext context) {
		WallData wallData = context.wallData();
		JumpData jumpData = context.jumpData();
		wallData.setWallSlideCollisionDir3DData(-1);
		if (player instanceof LocalPlayer) {
			jumpData.setJumpReleaseGraceTicks(0);
		}
	}

	@Override
	public void onClientTick(Player player, ParkourContext context) {
		if (FMLEnvironment.dist.isClient()) {
//			if (player.isLocalPlayer()) {
//				WallData jumpData = context.jumpData();
//				if (((LocalPlayer)player).input.jumping) {
//					// 如果一直按着跳跃键，重置宽限期
//					jumpData.setJumpReleaseGraceTicks(WALL_SLIDE_GRACE_PERIOD);
//				}else {
//					int grace = jumpData.getJumpReleaseGraceTicks();
//					if (grace > 0) {
//						jumpData.setJumpReleaseGraceTicks(grace - 1);
//					}
//				}
//			}
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

		return ParkourConfig.enableWallSlide
			&& isFallingOnAir
    			&& WallSlideLogic.findAvailableWallDirection(player) != null // 使用 v2 Sensor 检测
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToBehavior(player);
	}

	@Override
	public boolean canEnter(Player player, ParkourContext context) {
		return isBaseValid(player);
	}

	@Override
	public boolean isValid(Player player, ParkourContext context) {
		return isBaseValid(player);
	}
}