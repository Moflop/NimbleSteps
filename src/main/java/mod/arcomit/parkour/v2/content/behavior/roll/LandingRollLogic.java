package mod.arcomit.parkour.v2.content.behavior.roll;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.init.NsSounds;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.core.context.GroundMovementData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
public class LandingRollLogic {
	public static final int LANDING_ROLL_DURATION = 8; // 落地翻滚默认持续时间（以刻为单位）
	private static final float ROLL_SOUND_VOLUME = 1.0f;
	private static final float ROLL_SOUND_PITCH = 1.0f;

	public static void performLandingRoll(Player player, GroundMovementData groundData, LivingFallEvent event) {
		event.setDamageMultiplier(0);
		event.setCanceled(true);
		groundData.setLandingRollDuration(LANDING_ROLL_DURATION); // 这里后续可以对接状态机

		Level level = player.level();
		if (level.isClientSide) {
			Minecraft.getInstance().getSoundManager().play(
				new EntityBoundSoundInstance(
					NsSounds.LANDING_ROLL.get(), SoundSource.PLAYERS,
					ROLL_SOUND_VOLUME, ROLL_SOUND_PITCH, player, player.getRandom().nextLong()));
		} else {
			level.playSound(
				null, player.getX(), player.getY(), player.getZ(),
				NsSounds.LANDING_ROLL.get(), SoundSource.PLAYERS,
				ROLL_SOUND_VOLUME, ROLL_SOUND_PITCH);
			player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0));
		}
	}

	public static boolean cannotSetLandingRollWindow(Player player, GroundMovementData groundData) {
		if (!PlayerStateUtils.fallWillTakeDamage(player)) {
			return true;
		}
		if (groundData.getLandingRollWindow() > 0) {
			return true;
		}
		if (!isValid(player)) {
			return true;
		}

		return false;
	}

	public static boolean isValid(Player player) {
		return ServerConfig.enableLandingRoll
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}
