package mod.arcomit.parkour.content.behavior.slide;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.core.context.GroundData;
import mod.arcomit.parkour.core.context.ParkourContext;
import net.minecraft.world.entity.player.Player;

public class SlideLogic {

	/**
	 * 执行滑铲的物理推力与角度偏转 (Tap-Strafing)
	 */
	public static void setCooldown(Player player, ParkourContext context) {
		GroundData groundData = context.groundData();
		groundData.setSlideCooldown(ParkourConfig.slideCooldown);
	}
}