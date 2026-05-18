package mod.arcomit.parkour.content.action.walljump;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class WallJumpVelocity {
	private static final double WALL_JUMP_FORWARD_HORIZONTAL = 0.7; // 墙跳向前水平速度
	private static final double WALL_JUMP_FORWARD_VERTICAL = 0.4; // 墙跳向前垂直速度
	private static final double WALL_JUMP_UPWARD_VERTICAL  = 0.4; // 墙跳向上垂直速度

	static Vec3 computeJumpVelocity(Player player, WallJumpType type) {
		return switch (type) {
			case UP -> new Vec3(0, WALL_JUMP_UPWARD_VERTICAL, 0);
			case PARALLEL, VIEW -> {
				Vec3 look = player.getLookAngle();
				Vec3 jumpDir = new Vec3(look.x, 0, look.z).normalize();
				yield jumpDir.scale(WALL_JUMP_FORWARD_HORIZONTAL)
					.add(0, WALL_JUMP_FORWARD_VERTICAL, 0);
			}
			default -> Vec3.ZERO;
		};
	}
}
