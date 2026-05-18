package mod.arcomit.parkour.content.behavior.base;

import mod.arcomit.parkour.content.behavior.wallclimb.WallClimbState;
import mod.arcomit.parkour.content.behavior.wallrun.WallRunState;
import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.input.ParkourInputActions;
import mod.arcomit.parkour.core.proxy.ParkourProxies;
import mod.arcomit.parkour.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.core.statemachine.state.IParkourState;
import mod.arcomit.parkour.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

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
				ParkourStates.CRAWL::get,
				ParkourInputActions.SLIDE,
				(player, context) -> !ParkourProxies.PLAYER_SERVICES_PROXY.isMoving(player)
			),

			// 2. 移动中（且向前）按滑铲键 -> 滑铲
			IParkourStateTransition.onInput(
				ParkourStates.SLIDE::get,
				ParkourInputActions.SLIDE,
				(player, context) -> ParkourProxies.PLAYER_SERVICES_PROXY.isMoving(player)
					&& ParkourProxies.INPUT_PROXY.getForwardImpulse(player) >= 0
			),

			// 3. 移动中（且向后）在地面按滑铲键 -> 后撤步
			IParkourStateTransition.onInput(
				ParkourStates.BACKSTEP::get,
				ParkourInputActions.SLIDE,
				(player, context) -> ParkourProxies.PLAYER_SERVICES_PROXY.isMoving(player)
					&& ParkourProxies.INPUT_PROXY.getForwardImpulse(player) < 0
					&& player.onGround()
			),

			// 4. 在摔落翻滚判断期摔落 -> 摔落翻滚
			new IParkourStateTransition() {
				@Override
				public IParkourState getTargetState() { return ParkourStates.LANDING_ROLL.get(); }

				@Override
				public boolean shouldTransitionOnFall(Player player, ParkourContext context, LivingFallEvent event) {
					event.setDamageMultiplier(0);
					event.setCanceled(true);
					return true;
				}
			},

			// 5. 在空中下落，靠近墙壁，距离上次跳跃在15tick内，有向前冲量并且按住跳跃键 -> 进入墙跑
			// 注意：放在滑墙之前，作为高优先级判定
			IParkourStateTransition.onLocalTick(
				ParkourStates.WALL_RUN::get,
				(player, context) -> ParkourProxies.INPUT_PROXY.getJumping(player)
					&& ParkourContext.get(player).jumpData().getTicksSinceLastJump() <= WallRunState.MAX_TICKS_SINCE_JUMP
					&& ParkourProxies.INPUT_PROXY.getForwardImpulse(player) > WallRunState.ZERO_THRESHOLD
			),

			IParkourStateTransition.onLocalTick(
				ParkourStates.WALL_CLIMB::get,
				(player, context) -> ParkourProxies.INPUT_PROXY.getJumping(player)
					&& ParkourContext.get(player).jumpData().getTicksSinceLastJump() <= WallClimbState.MAX_TICKS_SINCE_JUMP
					&& ParkourProxies.INPUT_PROXY.getForwardImpulse(player) > WallClimbState.ZERO_THRESHOLD
			),

			// 6. 在空中下落，贴近墙壁并按住跳跃键 -> 进入滑墙
			IParkourStateTransition.onLocalTick(
				ParkourStates.WALL_SLIDE::get,
				(player, context) -> ParkourProxies.INPUT_PROXY.getJumping(player)
			)
//
//			IParkourStateTransition.onTick(
//				ParkourStates.ARMHANG::get,
//				(player, context) -> true
//			)
		);
	}
}
