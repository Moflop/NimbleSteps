package mod.arcomit.parkour.v2.core.statemachine.state;

import mod.arcomit.parkour.v2.core.context.ParkourContext;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IParkourState {

	/**
	 * 默认的动画变体 ID
	 */
	public static final int DEFAULT_ANIM_VARIANT = 0;

	// 此处应用List.of
	List<IParkourStateTransition> getTransitions();

	default void onEnter(Player player, ParkourContext context) {
		boolean isClient = player.level().isClientSide();

		if (isClient) {
			if (player.isLocalPlayer()) {
				onSimulationEnter(player, context);
			}
			onClientEnter(player, context);

		} else {
			onSimulationEnter(player, context);
			onServerEnter(player, context);
		}
	}

	default  void onServerEnter(Player player, ParkourContext context) {}

	default void onSimulationEnter(Player player, ParkourContext context) {}

	default void onClientEnter(Player player, ParkourContext context) {}

	default void onExit(Player player, ParkourContext context) {
		boolean isClient = player.level().isClientSide();

		if (isClient) {
			if (player.isLocalPlayer()) {
				onSimulationExit(player, context);
			}
			onClientExit(player, context);

		} else {
			onSimulationExit(player, context);
			onServerExit(player, context);
		}
	}

	default  void onServerExit(Player player, ParkourContext context) {}

	default void onSimulationExit(Player player, ParkourContext context) {}

	default void onClientExit(Player player, ParkourContext context) {}

	default void onTick(Player player, ParkourContext context) {
		boolean isClient = player.level().isClientSide();

		if (isClient) {
			if (player.isLocalPlayer()) {
				onSimulationTick(player, context);
			}
			onClientTick(player, context);

		} else {
			onSimulationTick(player, context);
			onServerTick(player, context);
		}
	}

	/**
	 * 服务端 Tick：处理权威的数值扣除、判定等
	 */
	default void onServerTick(Player player, ParkourContext context) {}

	/**
	 * 核心模拟 Tick：专用于物理移动、速度向量修改等需要【双端预测】的逻辑。
	 * 该方法只会在 Server 和 LocalPlayer 上执行。
	 */
	default void onSimulationTick(Player player, ParkourContext context) {}

	/**
	 * 客户端通用 Tick
	 */
	default void onClientTick(Player player, ParkourContext context) {}

	/**
	 * 用于服务端校验是否能进入该状态
	 */
	default boolean canEnter(Player player) {
		return true;
	}

	/**
	 * 是否有效：用于判断是否可持续维持当前状态
	 */
	default boolean isValid(Player player) {
		return true;
	}

	/**
	 * 获取该状态下自定义的完整实体尺寸（包含碰撞箱宽、高以及视线高度）。
	 * 如果返回 null，则使用原版默认尺寸。
	 */
	default EntityDimensions getCustomDimensions(Player player) {
		return null;
	}

	/**
	 * 当状态机决定进入此状态时调用，用于生成并分配一个变体 ID。
	 * 默认返回 0（代表没有变体或默认动画）。
	 * 应尽量使用双端一致的随机数生成方式，确保服务器和客户端生成的变体 ID 一致，以避免部分动画不同步的情况。
	 */
	default int generateVariant(Player player) {
		return DEFAULT_ANIM_VARIANT;
	}

	/**
	 * 获取该状态下的姿势
	 * 如果返回 null, 则没有Pose
	 *
	 */
	default Pose getLinkedPose() {
		return null;
	}
}
