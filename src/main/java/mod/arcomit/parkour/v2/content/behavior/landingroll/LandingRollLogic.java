package mod.arcomit.parkour.v2.content.behavior.landingroll;

import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class LandingRollLogic {

	/**
	 * 消耗翻滚窗口
	 */
	public static void consumeWindow(Player player, ParkourContext context) {
		GroundData groundData = context.groundData();
		groundData.setLandingRollWindow(0);
	}

	public static void addSpeedEffect(Player player) {
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0));
	}
}