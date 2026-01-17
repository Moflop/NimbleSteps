package mod.arcomit.nimblesteps.event.state;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 跳跃状态处理器。
 *
 * @author Arcomit
 * @since 2026-01-03
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class JumpStateHandler {

	@SubscribeEvent
	public static void onJump(LivingEvent.LivingJumpEvent event) {
		if (event.getEntity() instanceof Player player) {
			NimbleStepsState state = NimbleStepsState.getNimbleState(player);
			state.setHasJumped(true);
			state.setTicksSinceLastJump(0);
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (player.onGround() && state.isHasJumped()) {
			state.setHasJumped(false);
		}
		if (state.getTicksSinceLastJump() < 100) {
			state.setTicksSinceLastJump(state.getTicksSinceLastJump() + 1);
		}
	}
}
