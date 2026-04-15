package mod.arcomit.parkour.v2.content.behavior.landingroll;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class LandingRollLogic {

	/**
	 * 执行翻滚的数据修改与服务端效果
	 */
	public static void applyRollEffects(Player player) {
		GroundData groundData = ParkourContext.get(player).groundData();
		groundData.setLandingRollWindow(0); // 消耗掉翻滚窗口

		if (!player.level().isClientSide) {
			// 服务端给予速度增益
			player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0));
		}
	}
}