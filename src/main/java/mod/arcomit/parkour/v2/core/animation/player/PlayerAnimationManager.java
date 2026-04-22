package mod.arcomit.parkour.v2.core.animation.player;

import com.zigythebird.playeranim.animation.PlayerAnimManager;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.enums.PlayState;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.init.PkRegistries;
import mod.arcomit.parkour.v2.core.animation.player.network.RequestPlayOneOffAnimC2SPayload;
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
import net.neoforged.neoforge.network.PacketDistributor;

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
	 * 播放一次性动画（如翻滚、受击），应只在LocalPlayer上调用，会自动同步请求服务器广播给其它玩家客户端播放同样的动画。
	 *
	 * @param player        目标玩家
	 * @param animationId     要播放的动作ID
	 * @param interruptible 是否会被无动画的状态打断
	 */
	public static void playOneOffAnimation(AbstractClientPlayer player, ResourceLocation animationId, boolean interruptible) {
		PlayerAnimManager manager = PlayerAnimationAccess.getPlayerAnimManager(player);
		UUID uuid = player.getUUID();

		if (ACTION_CONTROLLERS.containsKey(uuid)) {
			PlayerAnimationController oldController = ACTION_CONTROLLERS.get(uuid);
			oldController.stop();
			oldController.removeAllModifiers();
			manager.removeLayer(PARKOUR_ACTION_LAYER_ID);
		}

		PlayerAnimationController newActionController = new PlayerAnimationController(player, (animController, state, animationSetter) -> PlayState.CONTINUE);

		AbstractModifier modifier = ClientAnimationRegistry.getActionModifier(animationId, player);
		if (modifier != null) {
			newActionController.addModifierLast(modifier);
		}

		ACTION_CONTROLLERS.put(uuid, newActionController);
		newActionController.triggerAnimation(animationId, 0);
		manager.addAnimLayer(PARKOUR_ACTION_LAYER_ID, newActionController);
		ACTION_INTERRUPTIBLE.put(uuid, interruptible);

		if (player.isLocalPlayer()) {
			PacketDistributor.sendToServer(new RequestPlayOneOffAnimC2SPayload(animationId, interruptible));
		}
	}

	/**
	 * 播放当前状态的动画
	 */
	public static void playStateAnimation(Player player) {
		if (!(player instanceof AbstractClientPlayer clientPlayer)) {
			return;
		}

		StateData stateData = ParkourContext.get(player).stateData();
		IParkourState currentState = stateData.getState();

		if (currentState == null) {
			return;
		}

		UUID uuid = player.getUUID();

		// 1. 将获取目标状态动画的逻辑提前，因为打断逻辑需要用到它
		ResourceLocation stateId = PkRegistries.PARKOUR_STATE_REGISTRY.getKey(currentState);
		PlayerAnimation targetAnim = ClientAnimationRegistry.getAnimation(stateId, stateData.getAnimVariant());

		// 2. 处理高层级一次性动画的打断判定
		if (ACTION_CONTROLLERS.containsKey(uuid)) {
			PlayerAnimationController actionController = ACTION_CONTROLLERS.get(uuid);
			if (actionController.isActive()) {
				// 新逻辑：
				// targetAnim != null 代表新状态有专属动画，强制打断一次性动作以让位给状态动画
				// 如果 targetAnim 为 null，则根据 interruptible 标志位决定是否被无动画的状态打断
				boolean interruptible = ACTION_INTERRUPTIBLE.getOrDefault(uuid, true);
				if (targetAnim != null || interruptible) {
					actionController.stop();
				}
			}
		}

		PlayerAnimationController stateController = getOrCreateStateController(clientPlayer);
		PlayerAnimManager manager = PlayerAnimationAccess.getPlayerAnimManager(clientPlayer);

		// 3. 处理后续的状态动画播放逻辑
		if (targetAnim != null) {
			stateController.removeAllModifiers();

			IModifierFactory factory = ClientAnimationRegistry.getModifierFactory(stateId);
			if (factory != null) {
				factory.apply(stateController, clientPlayer, currentState, stateData.getAnimVariant());
			}

			int offsetTicks = stateData.getTicksInState();
			stateController.triggerAnimation(targetAnim.id, offsetTicks);
			manager.removeLayer(PARKOUR_STATE_LAYER_ID);
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