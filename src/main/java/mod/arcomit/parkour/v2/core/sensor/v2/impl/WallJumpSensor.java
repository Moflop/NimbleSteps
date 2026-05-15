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
public class WallJumpSensor implements ISensor {
	private static final double BOX_MIN_HEIGHT_RATIO = 0.0;
	private static final double BOX_MAX_HEIGHT_RATIO = 0.3;
	private static final double COLLISION_CHECK_DISTANCE = 0.2;
	private static final TagKey<Block> IGNORED_BLOCKS_TAG = ParkourTags.Blocks.CLIMBABLE;

	private Direction direction;
	public WallJumpSensor(Direction direction) {
		this.direction = direction;
	}

	@Override
	public List<AABB> getRenderBoxes(Player player) {
		List<AABB> boxes = new ArrayList<>();
		boxes.add(ISensor.calculateWorldBox(player, direction, BOX_MIN_HEIGHT_RATIO, BOX_MAX_HEIGHT_RATIO, COLLISION_CHECK_DISTANCE));
		return boxes;
	}

	@Override
	public boolean isColliding(Player player) {
		AABB feetBox = ISensor.calculateWorldBox(player, direction, BOX_MIN_HEIGHT_RATIO, BOX_MAX_HEIGHT_RATIO, COLLISION_CHECK_DISTANCE);
		return CollisionUtils.isBlockCollision(player.level(), feetBox, IGNORED_BLOCKS_TAG);
	}

	@Override
	public boolean shouldDebugRender(Player player) {
		return ISensor.super.shouldDebugRender(player) || SensorDebugRenderHandler.DEBUG_TYPE == DebugType.WALL_JUMP;
	}
}
