package mod.arcomit.parkour.v2.core.statemachine.state;

import mod.arcomit.parkour.v2.content.client.NsKeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface IParkourStateTransition {
	IParkourState getTargetState();

	default boolean shouldTransitionOnTick(Player player) {
		return false;
	}

	default boolean shouldTransitionOnFall(Player player, LivingFallEvent event) {
		return false;
	}

	default boolean shouldTransitionOnInput(LocalPlayer player, NsKeyMapping keyMapping) {
		return false;
	}

	default boolean shouldTransitionOnLocalTick(LocalPlayer player) {
		return false;
	}

	// =========语法糖233=========
	/**
	 * 构建一个基于 Tick 判断的状态转换规则（适用于双端）
	 * @param targetState 目标状态的 Supplier (例如 PkParkourStates.DEFAULT::get)
	 * @param condition 触发转换的条件
	 */
	static IParkourStateTransition onTick(Supplier<IParkourState> targetState, Predicate<Player> condition) {
		return new IParkourStateTransition() {
			@Override
			public IParkourState getTargetState() {
				return targetState.get();
			}
			@Override
			public boolean shouldTransitionOnTick(Player player) {
				return condition.test(player);
			}
		};
	}

	/**
	 * 构建一个基于按键输入的状态转换规则
	 */
	static IParkourStateTransition onInput(Supplier<IParkourState> targetState, NsKeyMapping targetKey) {
		return new IParkourStateTransition() {
			@Override
			public IParkourState getTargetState() { return targetState.get(); }
			@Override
			public boolean shouldTransitionOnInput(LocalPlayer player, NsKeyMapping keyMapping) {
				return keyMapping == targetKey;
			}
		};
	}

	/**
	 * 构建一个基于按键输入，且带有额外条件判断的状态转换规则
	 */
	static IParkourStateTransition onInput(Supplier<IParkourState> targetState, NsKeyMapping targetKey, Predicate<LocalPlayer> condition) {
		return new IParkourStateTransition() {
			@Override
			public IParkourState getTargetState() { return targetState.get(); }

			@Override
			public boolean shouldTransitionOnInput(LocalPlayer player, NsKeyMapping keyMapping) {
				// 当按键匹配，且玩家满足附加条件时，才触发转换
				return keyMapping == targetKey && condition.test(player);
			}
		};
	}

	/**
	 * 构建一个纯客户端本地 Tick 预测的状态转换规则 (适用于需要发包服务端校验的环境判定)
	 */
	static IParkourStateTransition onLocalTick(Supplier<IParkourState> targetState, Predicate<LocalPlayer> condition) {
		return new IParkourStateTransition() {
			@Override
			public IParkourState getTargetState() { return targetState.get(); }

			@Override
			public boolean shouldTransitionOnLocalTick(LocalPlayer player) {
				return condition.test(player);
			}
		};
	}
}
