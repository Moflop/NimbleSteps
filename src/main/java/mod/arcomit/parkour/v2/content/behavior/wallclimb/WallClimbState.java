package mod.arcomit.parkour.v2.content.behavior.wallclimb;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.context.WallData;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
			// 服务端/双端基础不合法判断（例如离开墙面或持续时间耗尽）
			IParkourStateTransition.onTick(
				PkParkourStates.DEFAULT::get,
				player -> !this.isValid(player)
			),
			// 客户端输入中断判断（松开跳跃或停止前进）
			IParkourStateTransition.onLocalTick(
				PkParkourStates.DEFAULT::get,
				player -> player.input.forwardImpulse <= ZERO_THRESHOLD || !player.input.jumping
			)
		);
	}

	@Override
	public void onEnter(Player player, ParkourContext context) {
		WallData wallData = ParkourContext.get(player).wallData();
		wallData.setWallClimbed(true);

		if (player instanceof LocalPlayer localPlayer) {
			localPlayer.sendPosition();
		}

		// 1. 获取当前系统时间
		LocalDateTime now = LocalDateTime.now();

		// 2. 定义时间格式（精确到秒）
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		// 3. 将时间格式化为字符串
		String formattedTime = now.format(formatter);

		System.out.println("当前系统时间（到秒）: " + formattedTime);
	}

	@Override
	public void onExit(Player player, ParkourContext context) {
		super.onExit(player, context);
		// 1. 获取当前系统时间
		LocalDateTime now = LocalDateTime.now();

		// 2. 定义时间格式（精确到秒）
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		// 3. 将时间格式化为字符串
		String formattedTime = now.format(formatter);

		System.out.println("当前系统时间（到秒）: " + formattedTime);
	}

	@Override
	public void onTick(Player player) {
		super.onTick(player);

		if (player instanceof RemotePlayer) return;

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
		return ServerConfig.enableWallRun // 或替换为 enableWallClimb 视 Config 定义而定
			&& player.isSprinting()
			&& !player.onGround()
			&& WallClimbLogic.checkWallCollision(player)
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	@Override
	public boolean canEnter(Player player) {
		ParkourContext context = ParkourContext.get(player);
		boolean hasNotClimbedWall = !context.wallData().isWallClimbed();
		boolean isFallingOnAir = player.getDeltaMovement().y() < 0 && !player.onGround();

		return isBaseValid(player)
			&& isFallingOnAir
			&& hasNotClimbedWall
			&& !PlayerStateUtils.fallWillTakeDamage(player);
	}

	@Override
	public boolean isValid(Player player) {
		StateData stateData = ParkourContext.get(player).stateData();
		return isBaseValid(player) && stateData.getTicksInState() < ServerConfig.wallClimbDuration;
	}
}