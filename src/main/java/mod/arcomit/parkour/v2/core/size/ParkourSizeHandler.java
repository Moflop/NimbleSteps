package mod.arcomit.parkour.v2.core.size;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = ParkourMod.MODID)
public class ParkourSizeHandler {

	@SubscribeEvent
	public static void onPlayerSize(EntityEvent.Size event) {
		if (event.getEntity() instanceof Player player) {
			if (player instanceof ServerPlayer serverPlayer) {
				if (serverPlayer.connection == null) {
					return; // 玩家还在登录加载阶段，不要碰他的 Attachment！
				}
			}

			StateData stateData = ParkourContext.get(player).stateData();

			IParkourState currentState = stateData.getState();
			if (currentState == null) return;

			EntityDimensions customSize = currentState.getCustomDimensions(player);
			if (customSize != null) {
				event.setNewSize(customSize);
			}
		}
	}
}