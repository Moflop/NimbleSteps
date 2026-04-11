package mod.arcomit.parkour.v2.core.statemachine;

import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.content.init.PkRegistries;
import mod.arcomit.parkour.v2.content.client.NsKeyMapping;
import mod.arcomit.parkour.v2.core.animation.ParkourAnimationManager;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.network.BroadcastStateChangeS2CPayload;
import mod.arcomit.parkour.v2.core.statemachine.network.RequestStateTransitionC2SPayload;
import mod.arcomit.parkour.v2.core.statemachine.network.SyncLocalPlayerStateS2CPayload;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * 跑酷状态机。
 * <p>
 * 仅负责管理状态的生命周期和转换逻辑，不持久化存储任何状态数据（数据存储在 {@link StateData} 中）。
 * 采用客户端本地预测与服务端权威校验的架构。
 *
 * @author Arcomit
 * @since 2026-03-09
 */
public class ParkourStateMachine {

	/**
	 * 每 tick 更新状态机逻辑。
	 * 处理服务端与客户端不同的逻辑分支：客户端进行本地预测以保证流畅性，服务端进行合法性校验并在环境变化时处理状态回退。
	 * * @param player 正在更新状态的玩家。
	 */
	public static void tick(Player player) {
		StateData stateData = ParkourContext.get(player).stateData();
		IParkourState currentState = stateData.getState();

		// 服务端自动同步的状态需刷新碰撞箱尺寸和动画
		if (stateData.getLastTickState() != currentState) {
			IParkourState lastState = stateData.getLastTickState();
			EntityDimensions oldDim = lastState != null ? lastState.getCustomDimensions(player) : null;
			EntityDimensions newDim = currentState != null ? currentState.getCustomDimensions(player) : null;

			if (oldDim != newDim) {
				player.refreshDimensions();
			}

			stateData.setLastTickState(currentState);
			player.setForcedPose(currentState.getLinkedPose());
			if (player.level().isClientSide()) {
				ParkourAnimationManager.syncStateAnimation(player);
			}
		}

		stateData.setTicksInState(stateData.getTicksInState() + 1);
		currentState.onTick(player);

		if (player.level().isClientSide()) {
			// 客户端本地预测状态转换，优先级高于服务端校验，以保证输入响应的绝对流畅性。

			if (player instanceof LocalPlayer localPlayer) {
				tryTickTransition(localPlayer, currentState);
			}
		}else {
			// 服务端权威校验：如果当前状态已不再合法（通常由于环境突变导致，如脚下悬空），强制退回默认状态。
			if (!currentState.isValid(player)) {
				IParkourState defaultState = PkParkourStates.DEFAULT.get();
				if (defaultState.isValid(player)) {
					transitionTo(player, defaultState);

					ResourceLocation defaultId = PkRegistries.PARKOUR_REGISTRY.getKey(defaultState);
					if (defaultId != null) {
						PacketDistributor.sendToPlayer((ServerPlayer) player,
							new SyncLocalPlayerStateS2CPayload(defaultId));
						PacketDistributor.sendToPlayersTrackingEntity(player,
							new BroadcastStateChangeS2CPayload(player.getId(), defaultId, 0));
					}
				}
			}
		}
	}

	/**
	 * 尝试在 tick 更新中自动转换状态。
	 * 遍历当前状态的转换规则，若玩家满足某一规则的 {@code shouldTransitionOnTick} 条件，则执行转换。
	 * <p>
	 * 仅限客户端本地玩家使用（在多人游戏中，客户端也会存在其他玩家的实体，故需排除）。
	 * 此方法由状态机的 {@link #tick} 方法自动调用，通常不应在外部手动调用。
	 * * @param player	   尝试转换状态的本地玩家。
	 * @param currentState 当前所处的状态。
	 */
	private static void tryTickTransition(LocalPlayer player, IParkourState currentState) {
		// 防止误调
		if (!player.isLocalPlayer()) {
			return;
		}
		List<IParkourStateTransition> transitions = currentState.getTransitions();
		for (int i = 0; i < transitions.size(); i++) {
			IParkourStateTransition transition = transitions.get(i);
			if (transition.shouldTransitionOnTick(player)) {
				localTransitionTo(player, transition.getTargetState());
				return;
			}
		}
	}

	/**
	 * 尝试根据按键输入转换状态。
	 * 如果传入的 {@code keyMapping} 能通过某一规则的 {@code shouldTransitionOnInput} 条件，则执行状态切换。
	 * <p>
	 * 仅限客户端本地玩家使用（在多人游戏中，客户端也会存在其他玩家的实体，故需排除）。
	 * 此方法通常由输入状态机控制器在捕获到按键时自动调用。
	 * * @param player	 尝试转换状态的本地玩家。
	 * @param keyMapping 触发当前事件的按键映射。
	 */
	public static void tryInputTransition(LocalPlayer player, NsKeyMapping keyMapping) {
		// 防止误调
		if (!player.isLocalPlayer()) {
			return;
		}
		StateData stateData = ParkourContext.get(player).stateData();
		IParkourState currentState = stateData.getState();
		List<IParkourStateTransition> transitions = currentState.getTransitions();
		for (int i = 0; i < transitions.size(); i++) {
			IParkourStateTransition transition = transitions.get(i);
			if (transition.shouldTransitionOnInput(player, keyMapping)) {
				localTransitionTo(player, transition.getTargetState());
				return;
			}
		}
	}

	/**
	 * 客户端预测状态转换逻辑（使用默认动画变体 0）。
	 * 执行本地状态转换，并向服务端发送数据包请求同步。
	 * * @param player	  需要转换状态的本地玩家。
	 * @param targetState 目标状态。
	 */
	public static void localTransitionTo(LocalPlayer player, IParkourState targetState) {
		int variant = targetState.generateVariant(player);
		localTransitionTo(player, targetState, variant);
	}

	/**
	 * 客户端预测状态转换逻辑。
	 * 执行本地状态转换，并向服务端发送数据包（{@link RequestStateTransitionC2SPayload}）请求同步。
	 * * @param player	  需要转换状态的本地玩家。
	 * @param targetState 目标状态。
	 * @param animVariant 动画变体 ID。
	 */
	public static void localTransitionTo(LocalPlayer player, IParkourState targetState, int animVariant) {
		if (!player.isLocalPlayer()) {
			return;
		}
		transitionTo(player, targetState, animVariant);

		// 发包请求服务端进行状态转换验证并广播
		ResourceLocation targetId = PkRegistries.PARKOUR_REGISTRY.getKey(targetState);
		if (targetId != null) {
			PacketDistributor.sendToServer(new RequestStateTransitionC2SPayload(targetId, animVariant));
		}
	}

	/**
	 * 双端通用的核心状态转换执行方法（使用默认动画变体 0）。
	 * * @param player 	  需要转换状态的玩家。
	 * @param targetState 目标状态。
	 */
	public static void transitionTo(Player player, IParkourState targetState) {
		transitionTo(player, targetState, 0);
	}

	/**
	 * 双端通用的核心状态转换执行方法。
	 * 负责触发状态的退出与进入生命周期钩子（{@code onExit} / {@code onEnter}），并处理物理碰撞箱的形变刷新。
	 * * @param player 	  需要转换状态的玩家。
	 * @param targetState 目标状态。
	 * @param animVariant 动画变体 ID。
	 */
	public static void transitionTo(Player player, IParkourState targetState, int animVariant) {
		StateData stateData = ParkourContext.get(player).stateData();
		IParkourState currentState = stateData.getState();

		if (currentState != null) {
			currentState.onExit(player);
		}

		stateData.setState(targetState);
		stateData.setTicksInState(0);
		stateData.setAnimVariant(animVariant);

		if (targetState != null) {
			targetState.onEnter(player);
		}

		EntityDimensions currentDim = currentState != null ? currentState.getCustomDimensions(player) : null;
		EntityDimensions targetDim = targetState != null ? targetState.getCustomDimensions(player) : null;

		// 如果前后两个状态对尺寸的要求不一样，说明发生了形变，自动触发刷新
		// （如果两个状态的尺寸要求相同，或者都为 null，则不浪费性能去刷新碰撞箱）
		if (currentDim != targetDim) {
			player.refreshDimensions();
		}

		stateData.setLastTickState(currentState);
		player.setForcedPose(currentState.getLinkedPose());
		if (player.level().isClientSide()) {
			ParkourAnimationManager.syncStateAnimation(player);
		}
	}

	/**
	 * 重置玩家的跑酷状态。
	 * 通常用于强制中断跑酷动作时的清理操作（如跨越维度）。
	 * @param player 需要重置状态的玩家。
	 */
	public static void resetState(Player player) {
		StateData stateData = ParkourContext.get(player).stateData();
		IParkourState defaultState = PkParkourStates.DEFAULT.get();

		if (stateData.getState() != defaultState) {
			transitionTo(player, defaultState);

			if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
				ResourceLocation defaultId = PkRegistries.PARKOUR_REGISTRY.getKey(defaultState);
				if (defaultId != null) {
					// 同步玩家本人的客户端
					PacketDistributor.sendToPlayer(
						serverPlayer, new SyncLocalPlayerStateS2CPayload(defaultId)
					);
					// 广播周围观察者的客户端
					PacketDistributor.sendToPlayersTrackingEntity(
						player, new BroadcastStateChangeS2CPayload(player.getId(), defaultId, 0)
					);
				}
			}
		}
	}
}