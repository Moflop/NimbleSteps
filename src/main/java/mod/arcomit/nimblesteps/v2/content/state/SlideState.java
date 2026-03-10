package mod.arcomit.nimblesteps.v2.content.state;

import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.v1.utils.PlayerStateUtils;
import mod.arcomit.nimblesteps.v2.content.context.GroundMovementData;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
import mod.arcomit.nimblesteps.v2.core.statemachine.state.IMovementState;
import mod.arcomit.nimblesteps.v2.core.statemachine.state.IMovementStateTransition;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 滑铲状态
 * * @author Arcomit
 */
public class SlideState implements IMovementState {
	public static final SlideState INSTANCE = new SlideState();

	@Override
	public Iterable<IMovementStateTransition> transitions() {
		return List.of(
			new IMovementStateTransition() {
				@Override
				public IMovementState targetState() {
					return DefaultState.INSTANCE;
				}

				@Override
				public boolean canTrigger(Player player) {
					GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
					return groundData.getSlideDuration() <= 0
						|| !isValid(player);
				}
			}
		);
	}

	@Override
	public void onEnter(Player player, IMovementState previousState) {
		player.setForcedPose(Pose.SWIMMING);
	}

	@Override
	public void onExit(Player player, IMovementStateTransition triggeredTransition) {
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

	public static boolean isValid(Player player) {
		return ServerConfig.enableSlide
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}