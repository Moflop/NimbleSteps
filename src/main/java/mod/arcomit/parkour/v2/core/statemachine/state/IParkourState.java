package mod.arcomit.parkour.v2.core.statemachine.state;

import mod.arcomit.parkour.v2.core.animation.player.PlayerAnimmation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IParkourState {

	// 此处应用List.of
	List<IParkourStateTransition> getTransitions();

	default void onEnter(Player player) {}

	default void onExit(Player player) {}

	default void onTick(Player player) {
		if (player.level().isClientSide()) {
			onClientTick(player);
			if (player.isLocalPlayer()) {
				onLocalPlayerTick(player);
			} else {
				onRemotePlayerTick(player);
			}
		} else {
			onServerTick(player);
		}
	}

	/**
	 * 服务端 Tick：处理权威的数值扣除、判定等
	 */
	default void onServerTick(Player player) {}

	/**
	 * 客户端通用 Tick：处理所有玩家都能看到的表现（粒子、音效、动画状态）
	 */
	default void onClientTick(Player player) {}

	/**
	 * 客户端本地玩家 Tick：处理只有自己视角的表现（FOV变化、相机倾斜、屏幕抖动）
	 */
	default void onLocalPlayerTick(Player player) {}

	/**
	 * 客户端其他玩家 Tick：通常不需要重写，除非有极其特殊的剔除逻辑，需要排除自己
	 */
	default void onRemotePlayerTick(Player player) {}

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
	 */
	default int generateVariant(Player player) {
		return 0;
	}

	/**
	 * 获取该状态下自定义的玩家动画。
	 * 如果返回 null，则没有动画。
	 */
	default PlayerAnimmation getLinkedAnimation(Player player) {
		return null;
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
