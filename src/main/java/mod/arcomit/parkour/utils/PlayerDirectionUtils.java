package mod.arcomit.parkour.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class PlayerDirectionUtils {

	public static boolean isLookingInDirectionHorizontal(Player player, Direction dir, float maxAngle) {
		// 直接获取玩家的水平旋转角（Yaw），Minecraft的Yaw范围是-180到180
		float playerYaw = player.getYRot();
		// 获取目标方向的水平旋转角
		float targetYaw = dir.toYRot();

		// 计算最小角度差，范围在0~180
		float diff = Math.abs(playerYaw - targetYaw) % 360.0f;
		if (diff > 180.0f) {
			diff = 360.0f - diff;
		}

		return diff <= maxAngle;
	}
}
