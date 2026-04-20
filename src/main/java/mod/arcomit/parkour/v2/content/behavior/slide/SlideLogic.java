package mod.arcomit.parkour.v2.content.behavior.slide;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SlideLogic {

	/**
	 * 执行滑铲的物理推力与角度偏转 (Tap-Strafing)
	 */
	public static void applySlidePhysics(Player player) {
		GroundData groundData = ParkourContext.get(player).groundData();
		groundData.setSlideCooldown(ServerConfig.slideCooldown);

		if (player instanceof LocalPlayer localPlayer) {
			Input playerInput = localPlayer.input;
			float forwardImpulse = playerInput.forwardImpulse;
			float leftImpulse = playerInput.leftImpulse;

			float yRotRad = player.getYRot() * Mth.DEG_TO_RAD;
			float sin = Mth.sin(yRotRad);
			float cos = Mth.cos(yRotRad);

			double motionX = leftImpulse * cos - forwardImpulse * sin;
			double motionZ = forwardImpulse * cos + leftImpulse * sin;

			if (!player.onGround()) {
				if (ServerConfig.enableTapStrafing) {
					double targetYRot = Math.toDegrees(Math.atan2(-motionX, motionZ));
					float currentYRot = player.getYRot();
					float diff = Mth.wrapDegrees((float)targetYRot - currentYRot);

					player.setYRot(currentYRot + diff);
					player.yRotO = player.getYRot();
				}
			}

			Vec3 motion = new Vec3(motionX, 0, motionZ).normalize().scale(ServerConfig.slideBoostSpeed);
			player.setDeltaMovement(player.getDeltaMovement().add(motion));
		}
	}
}