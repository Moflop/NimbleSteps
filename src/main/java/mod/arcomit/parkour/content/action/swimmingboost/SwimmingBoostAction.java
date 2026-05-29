package mod.arcomit.parkour.content.action.swimmingboost;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.content.action.swimmingboost.client.ClientSwimmingBoostSound;
import mod.arcomit.parkour.core.context.SwimData;
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
public class SwimmingBoostAction {
	private static final double ZERO_THRESHOLD = 1.0E-7; // 零阈值

	public static boolean execute(Player player, SwimData swimData) {
		if (!SwimmingBoostEligibilityChecker.check(player, swimData)) {
			return false;
		}

		swimData.setSwimmingBoostCooldown(ParkourConfig.swimmingBoostCooldown);
		Vec3 deltaMovement = player.getDeltaMovement();

		Vec3 boostDirection;
		boolean isMoving = deltaMovement.lengthSqr() >= ZERO_THRESHOLD;
		if (isMoving) {
			boostDirection = deltaMovement.normalize();
		} else {
			boostDirection = player.getLookAngle();
		}

		Vec3 boostVelocity = boostDirection.scale(ParkourConfig.swimmingBoostSpeedMultiplier);
		player.setDeltaMovement(deltaMovement.add(boostVelocity));

		return true;
	}
}