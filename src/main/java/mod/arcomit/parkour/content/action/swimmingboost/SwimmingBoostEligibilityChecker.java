package mod.arcomit.parkour.content.action.swimmingboost;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.core.context.SwimData;
import mod.arcomit.parkour.utils.PlayerStateUtils;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-29
 */
public class SwimmingBoostEligibilityChecker {
	/**
	 * 判定是否满足推进条件。
	 */
	public static boolean check(Player player, SwimData swimData) {
		return ParkourConfig.enableSwimmingBoost
			&& swimData.getSwimmingBoostCooldown() <= 0
			&& player.isSwimming()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}
