package mod.arcomit.parkour.content.behavior.armhang;

import mod.arcomit.parkour.core.context.WallData;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-21
 */
public class ArmhangLogic {
	private static final double HANGING_POINT_ADHESION_FACTOR = 0.1; // 抓握点吸附力

	public static void applyLevitateAndAdhesion(Player player, WallData wallData) {
		Direction wallCollisionDirection = wallData.getArmhangDir();
		if (wallCollisionDirection != null) {
			// 向固定的运动方向移动
			player.setDeltaMovement(Vec3.ZERO);

			// 墙面吸附
			Vec3 wallNormal = new Vec3(wallCollisionDirection.getStepX(), 0, wallCollisionDirection.getStepZ());
			Vec3 adhesionForce = wallNormal.scale(HANGING_POINT_ADHESION_FACTOR);
			boolean wasOnGround = player.onGround();
			player.move(MoverType.PLAYER, adhesionForce);
			if (wasOnGround) {
				// 恢复 onGround 状态，防止由于水平贴墙导致的意外状态结束
				player.setOnGround(true);
			}

			// 重置跌落伤害
			player.resetFallDistance();
		}
	}
}
