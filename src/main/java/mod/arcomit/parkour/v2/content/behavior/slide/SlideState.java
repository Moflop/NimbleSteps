package mod.arcomit.parkour.v2.content.behavior.slide;

import mod.arcomit.parkour.v2.core.context.GroundMovementData;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v2.content.behavior.base.DefaultState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 滑铲状态
 * * @author Arcomit
 */
public class SlideState implements IParkourState {
	public static final SlideState INSTANCE = new SlideState();

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
					return groundData.getSlideDuration() <= 0
						|| !SlideLogic.isValid(player);
				}
			}
		);
	}

	@Override
	public void onEnter(Player player, IParkourState previousState) {
		player.setForcedPose(Pose.SWIMMING);
	}

	@Override
	public void onExit(Player player, IParkourStateTransition triggeredTransition) {
		MovementStateContext context = MovementStateContext.get(player);
		context.getGroundData().setSlideDuration(0);
		player.setForcedPose(null);
	}

	@Override
	public void onTick(Player player) {
		GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
		int currentDuration = groundData.getSlideDuration();
		if (currentDuration > 0) {
			groundData.setSlideDuration(currentDuration - 1);
		}
	}

}