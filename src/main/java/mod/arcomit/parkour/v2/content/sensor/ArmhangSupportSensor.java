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
public class ArmhangSupportSensor extends AbstractBoxSensor {
	private final Direction direction;
	private final boolean topHeightFromEyeLevel;

	private static final double COLLISION_CHECK_DISTANCE = 0.5; // 碰撞检测距离
	// 底部碰撞检测高度比例，原版玩家站立为1.8格，眼睛高度为1.62，而34%约等于0.6格，确保玩家不会在脚部的位置（一格）触发垂挂
	private static final double BOTTOM_COLLISION_MIN_HEIGHT_RATIO = 0.34;

	public ArmhangSupportSensor(Direction direction, boolean topHeightFromEyeLevel) {
		super("armhang_support_" + (topHeightFromEyeLevel ? "eye" : "top") + direction.getName());
		this.direction = direction;
		this.topHeightFromEyeLevel = topHeightFromEyeLevel;
	}

	@Override
	protected AABB calculateWorldBox(Player player) {
		Vec3 playerPos = player.position();
		double playerBoxHalfWidth = player.getBbWidth() / 2;
		double playerBoxHeight = player.getBbHeight();
		double topHeight = topHeightFromEyeLevel ? player.getEyeHeight() : playerBoxHeight;
		double bottomOffset = playerBoxHeight * BOTTOM_COLLISION_MIN_HEIGHT_RATIO;

		// 支撑点碰撞箱
		AABB baseBox = new AABB(
			playerPos.x - playerBoxHalfWidth,
			playerPos.y + topHeight - bottomOffset,
			playerPos.z - playerBoxHalfWidth,
			playerPos.x + playerBoxHalfWidth,
			playerPos.y + topHeight,
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
