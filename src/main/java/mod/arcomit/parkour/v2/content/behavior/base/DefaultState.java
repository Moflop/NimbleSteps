package mod.arcomit.parkour.v2.content.behavior.base;

import mod.arcomit.parkour.v2.content.behavior.crawl.CrawlState;
import mod.arcomit.parkour.v2.content.behavior.roll.LandingRollState;
import mod.arcomit.parkour.v2.content.behavior.slide.SlideState;
import mod.arcomit.parkour.v2.core.context.GroundMovementData;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v2.content.behavior.wallslide.WallSlideState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-09
 */
public class DefaultState implements IParkourState {

	public static final DefaultState INSTANCE = new DefaultState();

	@Override
	public Iterable<IParkourStateTransition> transitions() {
		return List.of(
			new IParkourStateTransition() {
				@Override
				public IParkourState targetState() {
					return CrawlState.INSTANCE;
				}

				@Override
				public boolean canTrigger(Player player) {
					GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
					return groundData.isCrawling();
				}
			},
			new IParkourStateTransition() {
				@Override
				public IParkourState targetState() {
					return SlideState.INSTANCE;
				}

				@Override
				public boolean canTrigger(Player player) {
					GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
					return groundData.getSlideDuration() > 0;
				}
			},
			new IParkourStateTransition() {
				@Override
				public IParkourState targetState() {
					return LandingRollState.INSTANCE;
				}

				@Override
				public boolean canTrigger(Player player) {
					GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
					return groundData.getLandingRollDuration() > 0;
				}
			},
			new IParkourStateTransition() {
				@Override
				public IParkourState targetState() {
					return WallSlideState.INSTANCE;
				}

				@Override
				public boolean canTrigger(Player player) {
					return MovementStateContext.get(player).getWallData().isWallSliding();
				}
			}
		);
	}

	@Override
	public void onEnter(Player player, IParkourState previousState) {}

	@Override
	public void onExit(Player player, IParkourStateTransition triggeredTransition) {}

	@Override
	public void onTick(Player player) {}
}
