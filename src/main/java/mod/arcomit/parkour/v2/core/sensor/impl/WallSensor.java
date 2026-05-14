package mod.arcomit.parkour.v2.core.sensor.impl;

import mod.arcomit.parkour.v1.utils.CollisionUtils;
import mod.arcomit.parkour.v2.core.sensor.AbstractBoxSensor;
import mod.arcomit.parkour.v2.core.sensor.client.SensorDebugRenderer;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-13
 */
public class WallSensor extends AbstractBoxSensor {
	private final Direction direction;
	private final double minHeightRatio;
	private final double maxHeightRatio;
	private final double checkDistance;
	private final TagKey<Block> ignoredTag;
	private final DebugGroup debugGroup;

	public enum DebugGroup { WALL, JUMP }

	public WallSensor(Direction direction,
	                  double minHeightRatio,
	                  double maxHeightRatio,
	                  double checkDistance,
	                  TagKey<Block> ignoredTag,
	                  String id,
	                  DebugGroup debugGroup) {
		super(id);
		this.direction = direction;
		this.minHeightRatio = minHeightRatio;
		this.maxHeightRatio = maxHeightRatio;
		this.checkDistance = checkDistance;
		this.ignoredTag = ignoredTag;
		this.debugGroup = debugGroup;
	}

	@Override
	protected AABB calculateWorldBox(Player player) {
		Vec3 playerPos = player.position();
		double halfWidth = player.getBbWidth() / 2;
		double height = player.getBbHeight();

		AABB baseBox = new AABB(
			playerPos.x - halfWidth,
			playerPos.y + height * minHeightRatio,
			playerPos.z - halfWidth,
			playerPos.x + halfWidth,
			playerPos.y + height * maxHeightRatio,
			playerPos.z + halfWidth
		).deflate(0.001);

		double ox = direction.getStepX() * checkDistance;
		double oy = direction.getStepY() * checkDistance;
		double oz = direction.getStepZ() * checkDistance;

		return baseBox.expandTowards(ox, oy, oz);
	}

	@Override
	protected boolean checkCollision(Player player, AABB box) {
		return CollisionUtils.isBlockCollision(player.level(), box, ignoredTag);
	}

	@Override
	public boolean shouldDebugRender(Player player) {
		return debugGroup == DebugGroup.WALL
			? SensorDebugRenderer.RENDER_WALL
			: SensorDebugRenderer.RENDER_JUMP;
	}
}