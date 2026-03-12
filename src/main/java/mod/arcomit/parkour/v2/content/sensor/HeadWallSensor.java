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
 * 墙体碰撞探测器器，用于检测玩家特定方向（头/脚）是否有墙壁。
 *
 * @author Arcomit
 */
public class HeadWallSensor extends AbstractBoxSensor {
	private final Direction direction;

	private static final double HEAD_BOX_MIN_HEIGHT_RATIO = 0.85; // 头部检测箱最小高度比例
	private static final double COLLISION_CHECK_DISTANCE = 0.15;  // 墙面碰撞检测距离

	public HeadWallSensor(Direction direction) {
		super("head_wall" + direction.getName());
		this.direction = direction;
	}

	@Override
	protected AABB calculateWorldBox(Player player) {
		Vec3 playerPos = player.position();
		double playerBoxHalfWidth = player.getBbWidth() / 2;
		double playerBoxHeight = player.getBbHeight();

		// 头部碰撞箱，高度：从玩家整体碰撞箱高度的85%到碰撞箱顶端
		AABB baseBox = new AABB(
			playerPos.x - playerBoxHalfWidth,
			playerPos.y + playerBoxHeight * HEAD_BOX_MIN_HEIGHT_RATIO,
			playerPos.z - playerBoxHalfWidth,
			playerPos.x + playerBoxHalfWidth,
			playerPos.y + playerBoxHeight,
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
		return CollisionUtils.isBlockCollision(player.level(), box, NsTags.Blocks.COMMON_IGNORED_BLOCKS);
	}

	@Override
	protected boolean shouldDebugRender(Player player) {
		return SensorDebugRenderer.RENDER_WALL;
	}
}