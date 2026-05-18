package mod.arcomit.parkour.content.behavior.mount;

import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.context.WallData;
import mod.arcomit.parkour.core.statemachine.state.AbstractParkourState;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

/**
 * 上墙/翻越状态。
 *
 * @author Arcomit
 */
public class MountState extends AbstractParkourState {

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
		wallData.setMountDuration3DData(0);
		wallData.setObstaclesHeight(0);
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	@Override
	public boolean canEnter(Player player, ParkourContext context) {
		return MountLogic.canStartMountFromArmhang(player);
	}

	@Override
	public boolean isValid(Player player, ParkourContext context) {
		WallData wallData = context.wallData();
		return wallData.getMountDuration3DData() > 0; // 只要 Duration 还没清零就继续
	}
}