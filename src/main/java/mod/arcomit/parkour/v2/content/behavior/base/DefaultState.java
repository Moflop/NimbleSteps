package mod.arcomit.parkour.v2.content.behavior.base;

import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.client.NsKeyBindings;
import mod.arcomit.parkour.v2.content.client.NsKeyMapping;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 默认状态
 *
 * @author Arcomit
 * @since 2026-03-09
 */
public class DefaultState extends AbstractParkourState {

	public DefaultState() {
		registerTransitions(
			// 1. 静止时按滑铲键 -> 爬行
			IParkourStateTransition.onInput(
				PkParkourStates.CRAWL::get,
				NsKeyBindings.SLIDE_KEY,
				player -> !PlayerStateUtils.isPlayerMoving(player)
			),

			// 2. 移动中（且向前）按滑铲键 -> 滑铲
			IParkourStateTransition.onInput(
				PkParkourStates.SLIDE::get,
				NsKeyBindings.SLIDE_KEY,
				player -> PlayerStateUtils.isPlayerMoving(player)
					&& player.input.forwardImpulse >= 0
					&& PkParkourStates.SLIDE.get().canEnter(player)
			),

			// 3. 移动中（且向后）在地面按滑铲键 -> 后撤步
			IParkourStateTransition.onInput(
				PkParkourStates.BACKSTEP::get,
				NsKeyBindings.SLIDE_KEY,
				player -> PlayerStateUtils.isPlayerMoving(player)
					&& player.input.forwardImpulse < 0
					&& player.onGround()
					&& PkParkourStates.BACKSTEP.get().canEnter(player)
			)

		);
	}
}
