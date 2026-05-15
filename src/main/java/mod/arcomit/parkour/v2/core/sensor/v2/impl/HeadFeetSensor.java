package mod.arcomit.parkour.v2.core.sensor.v2.impl;

import mod.arcomit.parkour.v1.utils.CollisionUtils;
import mod.arcomit.parkour.v2.content.init.ParkourTags;
import mod.arcomit.parkour.v2.core.sensor.v2.ISensor;
import mod.arcomit.parkour.v2.core.sensor.v2.client.handler.DebugType;
import mod.arcomit.parkour.v2.core.sensor.v2.client.handler.SensorDebugRenderHandler;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-14
 */
public class HeadFeetSensor implements ISensor {
	private static final double FEET_BOX_MIN_HEIGHT_RATIO = 0.0;
	private static final double FEET_BOX_MAX_HEIGHT_RATIO = 0.3;
	private static final double HEAD_BOX_MIN_HEIGHT_RATIO = 0.85;
	private static final double HEAD_BOX_MAX_HEIGHT_RATIO = 1.0;
	private static final double COLLISION_CHECK_DISTANCE = 0.15;
	private static final TagKey<Block> IGNORED_BLOCKS_TAG = ParkourTags.Blocks.COMMON_IGNORED_BLOCKS;

	private Direction direction;
	public HeadFeetSensor(Direction direction) {
		this.direction = direction;
	}

	@Override
	public List<AABB> getRenderBoxes(Player player) {
		List<AABB> boxes = new ArrayList<>();
		boxes.add(ISensor.calculateWorldBox(player, direction, HEAD_BOX_MIN_HEIGHT_RATIO, HEAD_BOX_MAX_HEIGHT_RATIO, COLLISION_CHECK_DISTANCE));
		boxes.add(ISensor.calculateWorldBox(player, direction, FEET_BOX_MIN_HEIGHT_RATIO, FEET_BOX_MAX_HEIGHT_RATIO, COLLISION_CHECK_DISTANCE));
		return boxes;
	}

	@Override
	public boolean isColliding(Player player) {
		AABB headBox = ISensor.calculateWorldBox(player, direction, HEAD_BOX_MIN_HEIGHT_RATIO, HEAD_BOX_MAX_HEIGHT_RATIO, COLLISION_CHECK_DISTANCE);
		if (!CollisionUtils.isBlockCollision(player.level(), headBox, IGNORED_BLOCKS_TAG)) {
			return false;
		}
		AABB feetBox = ISensor.calculateWorldBox(player, direction, FEET_BOX_MIN_HEIGHT_RATIO, FEET_BOX_MAX_HEIGHT_RATIO, COLLISION_CHECK_DISTANCE);
		return CollisionUtils.isBlockCollision(player.level(), feetBox, IGNORED_BLOCKS_TAG);
	}

	@Override
	public boolean shouldDebugRender(Player player) {
		if (SensorDebugRenderHandler.DEBUG_TYPE == DebugType.WALL_RUN) {
			Direction facing = player.getDirection();
			return facing.getClockWise() == this.direction || facing.getCounterClockWise() == this.direction;
		}
		if (SensorDebugRenderHandler.DEBUG_TYPE == DebugType.WALL_CLIMB) {
			Direction facing = player.getDirection();
			return facing == this.direction;
		}
		return ISensor.super.shouldDebugRender(player) || SensorDebugRenderHandler.DEBUG_TYPE == DebugType.WALL_SLIDE;
	}
}
