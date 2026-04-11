package mod.arcomit.parkour.v2.content.behavior.crawl;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.client.NsKeyBindings;
import mod.arcomit.parkour.v2.content.client.NsKeyMapping;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 爬行状态
 *
 * @author Arcomit
 * @since 2026-03-20
 */
public class CrawlState extends AbstractParkourState {

	public CrawlState() {
		registerTransitions(
			// 环境不再支持爬行时，退回默认状态
			IParkourStateTransition.onTick(PkParkourStates.DEFAULT::get, player -> !this.isValid(player)),

			// 玩家按下取消键（滑铲键）时，退回默认状态
			IParkourStateTransition.onInput(PkParkourStates.DEFAULT::get, NsKeyBindings.SLIDE_KEY)
		);
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.SWIMMING;
	}

	@Override
	public boolean isValid(Player player) {
		return ServerConfig.enableCrawl
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isSwimming()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}
