package mod.arcomit.parkour.content.behavior.crawl;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.utils.PlayerStateUtils;
import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.input.ParkourInputActions;
import mod.arcomit.parkour.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

/**
 * 爬行状态
 *
 * @author Arcomit
 * @since 2026-03-20
 */
public class CrawlState extends AbstractParkourState {

	public CrawlState() {
		registerTransitions(
			// 玩家按下取消键（滑铲键）时，退回默认状态
			IParkourStateTransition.onInput(ParkourStates.DEFAULT::get, ParkourInputActions.SLIDE)
		);
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.SWIMMING;
	}

	/**
	 * 验证玩家当前环境是否满足滑墙条件
	 */
	public static boolean isBaseValid(Player player) {
		return ParkourConfig.enableCrawl
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isSwimming()
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
