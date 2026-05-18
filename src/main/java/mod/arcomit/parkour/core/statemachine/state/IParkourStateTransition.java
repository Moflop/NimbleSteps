package mod.arcomit.parkour.core.statemachine.state;

import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.input.ParkourInputActions;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public interface IParkourStateTransition {
	IParkourState getTargetState();

	default boolean shouldTransitionOnTick(Player player, ParkourContext context) {
		return false;
	}

	default boolean shouldTransitionOnFall(Player player, ParkourContext context, LivingFallEvent event) {
		return false;
	}

	default boolean shouldTransitionOnInput(Player player, ParkourContext context, ParkourInputActions inputAction) {
		return false;
	}

	default boolean shouldTransitionOnLocalTick(Player player, ParkourContext context) {
		return false;
	}

	// =========语法糖233=========
	/**
	 * 构建一个基于 Tick 判断的状态转换规则（适用于双端）
	 * @param targetState 目标状态的 Supplier
	 * @param condition 触发转换的条件 (接收 Player 和 ParkourContext)
	 */
	static IParkourStateTransition onTick(Supplier<IParkourState> targetState, BiPredicate<Player, ParkourContext> condition) {
		return new IParkourStateTransition() {
			@Override
			public IParkourState getTargetState() {
				return targetState.get();
			}
			@Override
			public boolean shouldTransitionOnTick(Player player, ParkourContext context) {
				return condition.test(player, context);
			}
		};
	}

	/**
	 * 构建一个基于按键输入的状态转换规则
	 */
	static IParkourStateTransition onInput(Supplier<IParkourState> targetState, ParkourInputActions targetAction) {
		return new IParkourStateTransition() {
			@Override
			public IParkourState getTargetState() { return targetState.get(); }
			@Override
			public boolean shouldTransitionOnInput(Player player, ParkourContext context, ParkourInputActions inputAction) {
				return inputAction == targetAction;
			}
		};
	}

	/**
	 * 构建一个基于按键输入，且带有额外条件判断的状态转换规则
	 */
	static IParkourStateTransition onInput(Supplier<IParkourState> targetState, ParkourInputActions targetAction, BiPredicate<Player, ParkourContext> condition) {
		return new IParkourStateTransition() {
			@Override
			public IParkourState getTargetState() { return targetState.get(); }

			@Override
			public boolean shouldTransitionOnInput(Player player, ParkourContext context, ParkourInputActions inputAction) {
				return inputAction == targetAction && condition.test(player, context);
			}
		};
	}

	/**
	 * 构建一个纯客户端本地 Tick 预测的状态转换规则 (适用于需要发包服务端校验的环境判定)
	 */
	static IParkourStateTransition onLocalTick(Supplier<IParkourState> targetState, BiPredicate<Player, ParkourContext> condition) {
		return new IParkourStateTransition() {
			@Override
			public IParkourState getTargetState() { return targetState.get(); }

			@Override
			public boolean shouldTransitionOnLocalTick(Player player, ParkourContext context) {
				return condition.test(player, context);
			}
		};
	}
}