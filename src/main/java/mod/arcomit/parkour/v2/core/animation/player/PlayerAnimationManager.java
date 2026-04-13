package mod.arcomit.parkour.v2.core.animation.player;

import com.zigythebird.playeranim.animation.PlayerAnimManager;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.enums.PlayState;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class PlayerAnimationManager {
	public static final int PARKOUR_LAYER_ID = 1000;

	// 缓存玩家的动画控制器
	private static final Map<UUID, PlayerAnimationController> CONTROLLERS = new HashMap<>();

	private static PlayerAnimationController getOrCreateController(AbstractClientPlayer player) {
		UUID uuid = player.getUUID();
		if (CONTROLLERS.containsKey(uuid)) {
			return CONTROLLERS.get(uuid);
		}
		
		PlayerAnimationController controller = new PlayerAnimationController(player, (animController, state, animationSetter) -> {
			// 由于我们将通过 triggerAnimation 手动触发，状态机只需保持 CONTINUE 即可
			return PlayState.CONTINUE;
		});

		CONTROLLERS.put(uuid, controller);
		return controller;
	}

	public static void syncStateAnimation(Player player) {
		if (!(player instanceof AbstractClientPlayer clientPlayer)) return;

		StateData stateData = ParkourContext.get(player).stateData();
		IParkourState currentState = stateData.getState();

		if (currentState == null) return;

		PlayerAnimmation targetAnim = currentState.getLinkedAnimation(player);
		PlayerAnimationController controller = getOrCreateController(clientPlayer);
		PlayerAnimManager manager = PlayerAnimationAccess.getPlayerAnimManager(clientPlayer);

		if (targetAnim != null) {
			int offsetTicks = stateData.getTicksInState();
			int variant = stateData.getAnimVariant();

			PlayerAnimModifierRegistry.applyModifiers(controller, clientPlayer, currentState, variant);

			controller.triggerAnimation(targetAnim.id, offsetTicks);
			manager.addAnimLayer(PARKOUR_LAYER_ID, controller);

		} else {
			controller.stop();
			controller.removeAllModifiers();
			manager.removeLayer(PARKOUR_LAYER_ID);
		}
	}

	/**
	 * 当本地玩家断开连接、退出存档时，清空所有动画缓存
	 */
	@SubscribeEvent
	public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
		CONTROLLERS.values().forEach(PlayerAnimationController::stop); // 停止所有动画
		CONTROLLERS.clear();
	}

	/**
	 * 当任何玩家实体（包括本地玩家和其他玩家）从客户端世界被移除时（比如走出了视距、死亡或下线）
	 * 及时清理他们的 UUID 缓存
	 */
	@SubscribeEvent
	public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
		// 确保是客户端世界，且离开的实体是客户端玩家
		if (event.getLevel().isClientSide() && event.getEntity() instanceof AbstractClientPlayer player) {
			PlayerAnimationController controller = CONTROLLERS.remove(player.getUUID());
			if (controller != null) {
				controller.stop(); // 停止动画，释放可能的内部资源
			}
		}
	}
}