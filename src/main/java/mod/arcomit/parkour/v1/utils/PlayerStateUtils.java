package mod.arcomit.parkour.v1.utils;

import mod.arcomit.parkour.ServerConfig;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-01-05
 */
public class PlayerStateUtils {

	/**
	 * 判断玩家是否能够进行跑酷动作。
	 * 观察者模式、骑马、睡觉、三叉戟激流、滑翔、飞行状态下均不能进行跑酷动作。
	 *
	 * @param player 需要判定的玩家实体。
	 * @return 如果玩家能够进行跑酷动作则返回 true。
	 */
	public static boolean isAbleToAction(Player player) {
		return !player.isSpectator()
			&& !player.isPassenger()
			&& !player.isSleeping()
			&& !player.isAutoSpinAttack()
			&& !player.isFallFlying()
			&& !player.getAbilities().flying
			&& (player.getForcedPose() != null || player.getPose() != Pose.SWIMMING);
	}

	/**
	 * 判断玩家是否正在移动。
	 *
	 * @param player 需要判定的玩家实体。
	 * @return 如果玩家正在移动则返回 true。
	 */
	public static boolean isPlayerMoving(LocalPlayer player) {
		Input input = player.input;
		return input.forwardImpulse != 0 || input.leftImpulse != 0;
	}

	/**
	 * 判断玩家如果坠落是否会受到伤害。
	 *
	 * @param player 需要判定的玩家实体。
	 * @return 如果玩家坠落时会受到伤害则返回 true。
	 */
	public static boolean fallWillTakeDamage(Player player) {
		return player.fallDistance > ServerConfig.safeFallHeight;
	}
}
