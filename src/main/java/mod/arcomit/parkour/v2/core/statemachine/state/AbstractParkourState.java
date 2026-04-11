package mod.arcomit.parkour.v2.core.statemachine.state;

import java.util.List;

public abstract class AbstractParkourState implements IParkourState {

	// 默认为空，零内存开销
	private List<IParkourStateTransition> transitions = List.of();

	/**
	 * 提供给子类在构造函数中调用的注册方法
	 */
	protected final void registerTransitions(IParkourStateTransition... transitions) {
		// 只在实例化时执行一次，永久缓存
		this.transitions = List.of(transitions);
	}

	@Override
	public final List<IParkourStateTransition> getTransitions() {
		return this.transitions; // 之后每 Tick 调用都是 0 性能损耗
	}
}