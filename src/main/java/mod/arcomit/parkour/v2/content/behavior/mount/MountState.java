package mod.arcomit.parkour.v2.content.behavior.mount;

import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.WallData;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

/**
 * 上墙/翻越状态。
 *
 * @author Arcomit
 */
public class MountState extends AbstractParkourState {

	public MountState() {
		// 持续时间耗尽时退出 Mount 状态
		registerTransitions(
			IParkourStateTransition.onTick(
				PkParkourStates.DEFAULT::get,
				player -> !this.isValid(player)
			)
		);
	}

	@Override
	public void onEnter(Player player, ParkourContext context) {
		MountLogic.startMount(player, context);
	}

	@Override
	public void onTick(Player player, ParkourContext context) {
		super.onTick(player, context);
		MountLogic.applyMountMovement(player, context);
	}

	@Override
	public void onExit(Player player, ParkourContext context) {
		WallData wallData = ParkourContext.get(player).wallData();
		wallData.setMountDuration(0);
		wallData.setObstaclesHeight(0);
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	@Override
	public boolean canEnter(Player player) {
		return MountLogic.canStartMountFromArmhang(player);
	}

	@Override
	public boolean isValid(Player player) {
		WallData wallData = ParkourContext.get(player).wallData();
		return wallData.getMountDuration() > 0; // 只要 Duration 还没清零就继续
	}
}