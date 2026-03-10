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
 * 落地翻滚状态
 * * @author Arcomit
 */
public class LandingRollState implements IMovementState {
	public static final LandingRollState INSTANCE = new LandingRollState();

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
					return groundData.getLandingRollDuration() <= 0
						|| !isValid(player);
				}
			}
		);
	}

	@Override
	public void onEnter(Player player, IMovementState previousState) {
		player.setForcedPose(Pose.SWIMMING);
		// todo:播放摄像机动画
	}

	@Override
	public void onExit(Player player, IMovementStateTransition triggeredTransition) {
		MovementStateContext context = MovementStateContext.get(player);
		context.getGroundData().setLandingRollDuration(0);
		player.setForcedPose(null);
	}

	@Override
	public void onTick(Player player) {
		GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
		int currentDuration = groundData.getLandingRollDuration();
		if (currentDuration > 0) {
			groundData.setLandingRollWindow(currentDuration - 1);
		}
	}

	public static boolean isValid(Player player) {
		return ServerConfig.enableLandingRoll
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}