package mod.arcomit.parkour.v2.core.statemachine;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 跑酷状态机的主控制器（双端通用），驱动状态机的更新
 *
 * @author Arcomit
 * @since 2026-03-09
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class ParkourController {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre event) {
		Player player = event.getEntity();
		ParkourStateMachine.tick(player);
	}


	@SubscribeEvent
	public static void onPlayerFall(LivingFallEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!PlayerStateUtils.fallWillTakeDamage(player)) return;
		ParkourStateMachine.tryFallTransition(player, event);
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		Player player = event.getEntity();
		ParkourStateMachine.resetState(player);
	}
}
