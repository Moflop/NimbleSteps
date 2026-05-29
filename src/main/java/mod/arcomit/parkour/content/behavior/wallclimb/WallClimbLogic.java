package mod.arcomit.parkour.content.behavior.wallclimb;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.core.context.WallData;
import mod.arcomit.parkour.core.sensor.impl.HeadFeetSensor;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 爬墙核心逻辑
 *
 * @author Arcomit
 */
public class WallClimbLogic {
	private static final double WALL_ADHESION_FORCE = 0.1;

	public static void setCollisionDir(Player player, WallData wallData) {
		Direction wallCollisionDir = player.getDirection();
		wallData.setWallClimbCollisionDir(wallCollisionDir);
	}

	/**
	 * 执行爬墙时的物理运动与音效
	 */
	public static void useWallClimbMovement(Player player) {
		// 垂直向上爬升
		player.setDeltaMovement(0, ParkourConfig.wallClimbSpeed, 0);

		// 墙面吸附
		Direction facing = player.getDirection();
		Vec3 facingVec = Vec3.atLowerCornerOf(facing.getNormal());
		Vec3 adhesionForce = facingVec.scale(WALL_ADHESION_FORCE);
		boolean wasOnGround = player.onGround();
		player.move(MoverType.PLAYER, adhesionForce);
		if (wasOnGround) {
			// 恢复 onGround 状态，防止由于水平贴墙导致的意外状态结束
			player.setOnGround(true);
		}

		player.resetFallDistance();
	}

	/**
	 * 检查玩家正前方是否满足爬墙的碰撞条件
	 */
	public static boolean checkWallCollision(Player player) {
		Direction facing = player.getDirection();
		return HeadFeetSensor.isValidCollision(player, facing);
	}
}