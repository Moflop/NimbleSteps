package mod.arcomit.parkour.v2.content.behavior.base;

import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.behavior.wallclimb.WallClimbState;
import mod.arcomit.parkour.v2.content.behavior.wallrun.WallRunState;
import mod.arcomit.parkour.v2.content.client.NsKeyBindings;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
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
				PkParkourStates.CRAWL::get,
				NsKeyBindings.SLIDE_KEY,
				player -> !PlayerStateUtils.isPlayerMoving(player)
					&& PkParkourStates.CRAWL.get().canEnter(player)
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
			),

			// 4. 在摔落翻滚判断期摔落 -> 摔落翻滚
			new IParkourStateTransition() {
				@Override
				public IParkourState getTargetState() { return PkParkourStates.LANDING_ROLL.get(); }

				@Override
				public boolean shouldTransitionOnFall(Player player, LivingFallEvent event) {
					if (PkParkourStates.LANDING_ROLL.get().canEnter(player)) {
						event.setDamageMultiplier(0);
						event.setCanceled(true);
						return true;
					}
					return false;
				}
			},

			// 5. 在空中下落，靠近墙壁，已经跳跃了至少15tick，有向前冲量并且按住跳跃键 -> 进入墙跑
			// 注意：放在滑墙之前，作为高优先级判定
			IParkourStateTransition.onLocalTick(
				PkParkourStates.WALL_RUN::get,
				player -> player.input.jumping
					&& ParkourContext.get(player).jumpData().getTicksSinceLastJump() <= WallRunState.MAX_TICKS_SINCE_JUMP
					&& player.input.forwardImpulse > WallRunState.ZERO_THRESHOLD
					&& PkParkourStates.WALL_RUN.get().canEnter(player)
			),

			IParkourStateTransition.onLocalTick(
				PkParkourStates.WALL_CLIMB::get,
				player -> player.input.jumping
					&& ParkourContext.get(player).jumpData().getTicksSinceLastJump() <= WallClimbState.MAX_TICKS_SINCE_JUMP
					&& player.input.forwardImpulse > WallClimbState.ZERO_THRESHOLD
					&& PkParkourStates.WALL_CLIMB.get().canEnter(player)
			),

			// 6. 在空中下落，贴近墙壁并按住跳跃键 -> 进入滑墙
			IParkourStateTransition.onLocalTick(
				PkParkourStates.WALL_SLIDE::get,
				player -> player.input.jumping && PkParkourStates.WALL_SLIDE.get().canEnter(player)
			),

			IParkourStateTransition.onTick(
				PkParkourStates.ARMHANG::get,
				player -> PkParkourStates.ARMHANG.get().canEnter(player)
			)
		);
	}
}
