package mod.arcomit.parkour.v2.core.animation.player.v2;

import com.zigythebird.playeranim.animation.PlayerAnimManager;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.enums.PlayState;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.init.PkRegistries;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
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

	public static final int PARKOUR_STATE_LAYER_ID = 1000;
	public static final int PARKOUR_ACTION_LAYER_ID = 2000;

	// 分别缓存状态控制器和动作控制器
	private static final Map<UUID, PlayerAnimationController> STATE_CONTROLLERS = new HashMap<>();
	private static final Map<UUID, PlayerAnimationController> ACTION_CONTROLLERS = new HashMap<>();

	// 记录一次性动画是否可被状态切换打断
	private static final Map<UUID, Boolean> ACTION_INTERRUPTIBLE = new HashMap<>();

	private static PlayerAnimationController getOrCreateStateController(AbstractClientPlayer player) {
		return STATE_CONTROLLERS.computeIfAbsent(player.getUUID(), uuid ->
			new PlayerAnimationController(player, (animController, state, animationSetter) -> PlayState.CONTINUE));
	}

	private static PlayerAnimationController getOrCreateActionController(AbstractClientPlayer player) {
		return ACTION_CONTROLLERS.computeIfAbsent(player.getUUID(), uuid ->
			new PlayerAnimationController(player, (animController, state, animationSetter) -> PlayState.CONTINUE));
	}

	/**
	 * 播放一次性动画（如翻滚、受击）
	 *
	 * @param player        目标玩家
	 * @param animation     要播放的动作
	 * @param interruptible 是否会被状态改变（如落地、跳跃）打断
	 */
	public static void playOneOffAnimation(AbstractClientPlayer player, ParkourAnimation animation, boolean interruptible) {
		PlayerAnimationController actionController = getOrCreateActionController(player);
		PlayerAnimManager manager = PlayerAnimationAccess.getPlayerAnimManager(player);

		actionController.stop(); // 停止上一个动作
		actionController.removeAllModifiers();

		// 触发动作并放置在更高优先级图层
		actionController.triggerAnimation(animation.id, 0);
		manager.addAnimLayer(PARKOUR_ACTION_LAYER_ID, actionController);

		ACTION_INTERRUPTIBLE.put(player.getUUID(), interruptible);
	}

	/**
	 * 由状态机每 Tick 触发，同步常驻状态的动画
	 */
	public static void syncStateAnimation(Player player) {
		if (!(player instanceof AbstractClientPlayer clientPlayer)) {
			return;
		}

		StateData stateData = ParkourContext.get(player).stateData();
		IParkourState currentState = stateData.getState();

		if (currentState == null) {
			return;
		}

		UUID uuid = player.getUUID();

		// 处理高层级一次性动画的打断判定
		if (ACTION_CONTROLLERS.containsKey(uuid)) {
			PlayerAnimationController actionController = ACTION_CONTROLLERS.get(uuid);
			if (actionController.isActive() && ACTION_INTERRUPTIBLE.getOrDefault(uuid, true)) {
				actionController.stop();
			}
		}

		ResourceLocation stateId = PkRegistries.PARKOUR_REGISTRY.getKey(currentState);
		ParkourAnimation targetAnim = ClientAnimationRegistry.getAnimation(stateId);

		PlayerAnimationController stateController = getOrCreateStateController(clientPlayer);
		PlayerAnimManager manager = PlayerAnimationAccess.getPlayerAnimManager(clientPlayer);

		if (targetAnim != null) {
			stateController.removeAllModifiers();

			IModifierFactory factory = ClientAnimationRegistry.getModifierFactory(stateId);
			if (factory != null) {
				factory.apply(stateController, clientPlayer, currentState, stateData.getAnimVariant());
			}

			int offsetTicks = stateData.getTicksInState();
			stateController.triggerAnimation(targetAnim.id, offsetTicks);
			manager.addAnimLayer(PARKOUR_STATE_LAYER_ID, stateController);
		} else {
			stateController.stop();
			stateController.removeAllModifiers();
			manager.removeLayer(PARKOUR_STATE_LAYER_ID);
		}
	}

	@SubscribeEvent
	public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
		STATE_CONTROLLERS.values().forEach(PlayerAnimationController::stop);
		ACTION_CONTROLLERS.values().forEach(PlayerAnimationController::stop);
		STATE_CONTROLLERS.clear();
		ACTION_CONTROLLERS.clear();
		ACTION_INTERRUPTIBLE.clear();
	}

	@SubscribeEvent
	public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
		if (event.getLevel().isClientSide() && event.getEntity() instanceof AbstractClientPlayer player) {
			UUID uuid = player.getUUID();

			PlayerAnimationController stateController = STATE_CONTROLLERS.remove(uuid);
			if (stateController != null) stateController.stop();

			PlayerAnimationController actionController = ACTION_CONTROLLERS.remove(uuid);
			if (actionController != null) actionController.stop();

			ACTION_INTERRUPTIBLE.remove(uuid);
		}
	}
}