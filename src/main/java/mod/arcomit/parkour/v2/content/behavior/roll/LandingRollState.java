package mod.arcomit.parkour.v2.content.behavior.roll;

import mod.arcomit.parkour.v2.core.context.GroundMovementData;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v2.content.behavior.base.DefaultState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 落地翻滚状态
 * * @author Arcomit
 */
public class LandingRollState implements IParkourState {
	public static final LandingRollState INSTANCE = new LandingRollState();

	@Override
	public Iterable<IParkourStateTransition> transitions() {
		return List.of(
			new IParkourStateTransition() {
				@Override
				public IParkourState targetState() {
					return DefaultState.INSTANCE;
				}

				@Override
				public boolean canTrigger(Player player) {
					GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
					return groundData.getLandingRollDuration() <= 0
						|| !LandingRollLogic.isValid(player);
				}
			}
		);
	}

	@Override
	public void onEnter(Player player, IParkourState previousState) {
		player.setForcedPose(Pose.SWIMMING);
		// todo:播放摄像机动画
	}

	@Override
	public void onExit(Player player, IParkourStateTransition triggeredTransition) {
		GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
		groundData.setLandingRollDuration(0);
		player.setForcedPose(null);
	}

	@Override
	public void onTick(Player player) {
		GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
		int currentDuration = groundData.getLandingRollDuration();
		if (currentDuration > 0) {
			groundData.setLandingRollDuration(currentDuration - 1);
		}
	}

}