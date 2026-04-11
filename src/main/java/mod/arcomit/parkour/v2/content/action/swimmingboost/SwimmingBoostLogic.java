package mod.arcomit.parkour.v2.content.action.swimmingboost;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.core.context.SwimData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 水中推进逻辑类。
 * * @author Arcomit
 */
public class SwimmingBoostLogic {
	private static final double ZERO_THRESHOLD = 1.0E-7; // 零阈值

	public static final float SWIMMING_BOOST_SOUND_VOLUME = 0.9f;
	public static final float SWIMMING_BOOST_SOUND_PITCH = 0.8f;

	/**
	 * 执行水中推进核心物理逻辑与音效。
	 */
	public static void useSwimmingBoost(Player player, SwimData swimData) {
		swimData.setSwimmingBoostCooldown(ServerConfig.swimmingBoostCooldown);
		Vec3 deltaMovement = player.getDeltaMovement();

		Vec3 boostDirection;
		boolean isMoving = deltaMovement.lengthSqr() >= ZERO_THRESHOLD;
		if (isMoving) {
			boostDirection = deltaMovement.normalize();
		} else {
			boostDirection = player.getLookAngle();
		}

		Vec3 boostVelocity = boostDirection.scale(ServerConfig.swimmingBoostSpeedMultiplier);
		player.setDeltaMovement(deltaMovement.add(boostVelocity));

		// 双端音效播放逻辑
		Level level = player.level();
		if (level.isClientSide) {
			Minecraft.getInstance().getSoundManager().play(
				new EntityBoundSoundInstance(
					SoundEvents.AMBIENT_UNDERWATER_ENTER,
					SoundSource.PLAYERS,
					SWIMMING_BOOST_SOUND_VOLUME,
					SWIMMING_BOOST_SOUND_PITCH,
					player,
					player.getRandom().nextLong()));
		} else {
			level.playSound(
				null,
				player.getX(),
				player.getY(),
				player.getZ(),
				SoundEvents.AMBIENT_UNDERWATER_ENTER,
				SoundSource.PLAYERS,
				SWIMMING_BOOST_SOUND_VOLUME,
				SWIMMING_BOOST_SOUND_PITCH);
		}
	}

	/**
	 * 判定是否满足推进条件。
	 */
	public static boolean canSwimmingBoost(Player player, SwimData swimData) {
		return ServerConfig.enableSwimmingBoost
			&& swimData.getSwimmingBoostCooldown() <= 0
			&& player.isSwimming()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}