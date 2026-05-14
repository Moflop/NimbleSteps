package mod.arcomit.parkour.v2.core.client.animation.player;

import com.zigythebird.playeranim.animation.PlayerAnimManager;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.enums.PlayState;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.init.ParkourRegistries;
import mod.arcomit.parkour.v2.core.client.animation.player.network.RequestPlayOneOffAnimC2SPayload;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
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

	public static final PlayerAnimationManager INSTANCE = new PlayerAnimationManager();

	public static final int PARKOUR_STATE_LAYER_ID = 1000;
	public static final int PARKOUR_ACTION_LAYER_ID = 2000;

	private final Map<UUID, PlayerAnimationController> stateControllers = new HashMap<>();
	private final Map<UUID, PlayerAnimationController> actionControllers = new HashMap<>();
	private final Map<UUID, Boolean> actionInterruptible = new HashMap<>();

	private PlayerAnimationManager() {}

	private PlayerAnimationController getOrCreateStateController(AbstractClientPlayer player) {
		return stateControllers.computeIfAbsent(player.getUUID(), uuid ->
			new PlayerAnimationController(player, (animController, state, animationSetter) -> PlayState.CONTINUE));
	}

	private PlayerAnimationController getOrCreateActionController(AbstractClientPlayer player) {
		return actionControllers.computeIfAbsent(player.getUUID(), uuid ->
			new PlayerAnimationController(player, (animController, state, animationSetter) -> PlayState.CONTINUE));
	}

	/**
	 * 播放一次性动画（如翻滚、受击）
	 */
	public void playOneOffAnimation(AbstractClientPlayer player, ResourceLocation animationId, boolean interruptible) {
		PlayerAnimManager manager = PlayerAnimationAccess.getPlayerAnimManager(player);
		UUID uuid = player.getUUID();

		if (actionControllers.containsKey(uuid)) {
			PlayerAnimationController oldController = actionControllers.get(uuid);
			oldController.stop();
			oldController.removeAllModifiers();
			manager.removeLayer(PARKOUR_ACTION_LAYER_ID);
		}

		PlayerAnimationController newActionController = new PlayerAnimationController(player, (animController, state, animationSetter) -> PlayState.CONTINUE);

		AbstractModifier modifier = ClientAnimationRegistry.getActionModifier(animationId, player);
		if (modifier != null) {
			newActionController.addModifierLast(modifier);
		}

		actionControllers.put(uuid, newActionController);
		newActionController.triggerAnimation(animationId, 0);
		manager.addAnimLayer(PARKOUR_ACTION_LAYER_ID, newActionController);
		actionInterruptible.put(uuid, interruptible);

		if (player.isLocalPlayer()) {
			PacketDistributor.sendToServer(new RequestPlayOneOffAnimC2SPayload(animationId, interruptible));
		}
	}

	/**
	 * 播放当前状态的动画
	 */
	public void playStateAnimation(AbstractClientPlayer player) {
		StateData stateData = ParkourContext.get(player).stateData();
		IParkourState currentState = stateData.getState();

		if (currentState == null) {
			return;
		}

		UUID uuid = player.getUUID();

		ResourceLocation stateId = ParkourRegistries.PARKOUR_STATE_REGISTRY.getKey(currentState);
		PlayerAnimation targetAnim = ClientAnimationRegistry.getAnimation(stateId, stateData.getAnimVariant());

		if (actionControllers.containsKey(uuid)) {
			PlayerAnimationController actionController = actionControllers.get(uuid);
			if (actionController.isActive()) {
				boolean interruptible = actionInterruptible.getOrDefault(uuid, true);
				if (targetAnim != null || interruptible) {
					actionController.stop();
				}
			}
		}

		PlayerAnimationController stateController = getOrCreateStateController(player);
		PlayerAnimManager manager = PlayerAnimationAccess.getPlayerAnimManager(player);

		if (targetAnim != null) {
			stateController.removeAllModifiers();

			IModifierFactory factory = ClientAnimationRegistry.getModifierFactory(stateId);
			if (factory != null) {
				factory.apply(stateController, player, currentState, stateData.getAnimVariant());
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

	public void clearAll() {
		stateControllers.values().forEach(PlayerAnimationController::stop);
		actionControllers.values().forEach(PlayerAnimationController::stop);
		stateControllers.clear();
		actionControllers.clear();
		actionInterruptible.clear();
	}

	public void removePlayer(UUID uuid) {
		PlayerAnimationController stateController = stateControllers.remove(uuid);
		if (stateController != null) stateController.stop();

		PlayerAnimationController actionController = actionControllers.remove(uuid);
		if (actionController != null) actionController.stop();

		actionInterruptible.remove(uuid);
	}

	@SubscribeEvent
	public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
		INSTANCE.clearAll();
	}

	@SubscribeEvent
	public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
		if (event.getLevel().isClientSide() && event.getEntity() instanceof AbstractClientPlayer player) {
			INSTANCE.removePlayer(player.getUUID());
		}
	}
}