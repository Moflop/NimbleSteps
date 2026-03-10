package mod.arcomit.nimblesteps.v2.content.state;

import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.v1.utils.PlayerStateUtils;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
import mod.arcomit.nimblesteps.v2.core.statemachine.state.IMovementState;
import mod.arcomit.nimblesteps.v2.core.statemachine.state.IMovementStateTransition;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 爬行状态。
 *
 * @author Arcomit
 * @since 2026-03-09
 */
public class CrawlState implements IMovementState {

	public static final CrawlState INSTANCE = new CrawlState();

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
					MovementStateContext context = MovementStateContext.get(player);
					return !context.getGroundData().isCrawling() || !isValid(player);
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
		MovementStateContext.get(player).getGroundData().setCrawling(false);
		player.setForcedPose(null);
	}

	@Override
	public void onTick(Player player) {}

	public static boolean isValid(Player player) {
		return ServerConfig.enableCrawl
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isSwimming()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}