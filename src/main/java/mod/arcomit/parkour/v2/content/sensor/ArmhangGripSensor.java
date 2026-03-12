package mod.arcomit.parkour.v2.content.sensor;

import mod.arcomit.parkour.v1.init.NsTags;
import mod.arcomit.parkour.v1.utils.CollisionUtils;
import mod.arcomit.parkour.v2.core.sensor.AbstractBoxSensor;
import mod.arcomit.parkour.v2.core.sensor.SensorDebugRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
public class ArmhangGripSensor extends AbstractBoxSensor {
	private final Direction direction;
	private final boolean bottomHeightFromEyeLevel;

	private static final double COLLISION_CHECK_DISTANCE = 0.5; // 碰撞检测距离

	public ArmhangGripSensor(Direction direction, boolean bottomHeightFromEyeLevel) {
		super("armhang_grip_" + (bottomHeightFromEyeLevel ? "eye" : "top") + direction.getName());
		this.direction = direction;
		this.bottomHeightFromEyeLevel = bottomHeightFromEyeLevel;
	}

	@Override
	protected AABB calculateWorldBox(Player player) {
		Vec3 playerPos = player.position();
		double playerBoxHalfWidth = player.getBbWidth() / 2;
		double playerBoxHeight = player.getBbHeight();
		double bottomHeight = bottomHeightFromEyeLevel ? player.getEyeHeight() : playerBoxHeight;
		double topOffset = playerBoxHeight - player.getEyeHeight();// 顶端碰撞箱到眼睛碰撞箱之间的高度

		// 抓握点碰撞箱
		AABB baseBox = new AABB(
			playerPos.x - playerBoxHalfWidth,
			playerPos.y + bottomHeight,
			playerPos.z - playerBoxHalfWidth,
			playerPos.x + playerBoxHalfWidth,
			playerPos.y + bottomHeight + topOffset,
			playerPos.z + playerBoxHalfWidth
		).deflate(0.001);

		// 向对应方向扩展碰撞检测距离
		double offsetX = direction.getStepX() * COLLISION_CHECK_DISTANCE;
		double offsetY = direction.getStepY() * COLLISION_CHECK_DISTANCE;
		double offsetZ = direction.getStepZ() * COLLISION_CHECK_DISTANCE;

		return baseBox.expandTowards(offsetX, offsetY, offsetZ);
	}

	@Override
	protected boolean checkCollision(Player player, AABB box) {
		return CollisionUtils.isBlockCollision(player.level(), box, NsTags.Blocks.SCAFFOLDING_BLOCKS);
	}

	@Override
	protected boolean shouldDebugRender(Player player) {
		return SensorDebugRenderer.RENDER_ARMHANG && player.getDirection() == this.direction;
	}
}
