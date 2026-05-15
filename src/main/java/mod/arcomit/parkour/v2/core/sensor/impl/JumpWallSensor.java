//package mod.arcomit.parkour.v2.core.sensor.impl;
//
//import mod.arcomit.parkour.v1.utils.CollisionUtils;
//import mod.arcomit.parkour.v2.content.init.ParkourTags;
//import mod.arcomit.parkour.v2.core.sensor.SensorCache;
//import mod.arcomit.parkour.v2.core.sensor.SensorCategory;
//import mod.arcomit.parkour.v2.core.sensor.SensorKey;
//import mod.arcomit.parkour.v2.core.sensor.SensorResult;
//import mod.arcomit.parkour.v2.core.sensor.client.SensorDebugRenderer;
//import net.minecraft.core.Direction;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.phys.AABB;
//import net.minecraft.world.phys.Vec3;
//
///**
// * 墙跳墙体碰撞检测器。
// *
// * @author Arcomit
// */
//public final class JumpWallSensor {
//	private static final double FEET_BOX_MAX_HEIGHT_RATIO = 0.3;
//	private static final double COLLISION_CHECK_DISTANCE = 0.2;
//
//	public static SensorResult check(Player player, Direction direction) {
//		SensorKey key = new SensorKey(SensorCategory.JUMP_WALL, direction);
//		return SensorCache.getOrCompute(player, key, () -> {
//			AABB box = calculateWorldBox(player, direction);
//			boolean colliding = CollisionUtils.isBlockCollision(player.level(), box, ParkourTags.Blocks.CLIMBABLE);
//			return new SensorResult(box, colliding);
//		});
//	}
//
//	public static boolean shouldDebugRender(Player player) {
//		return SensorDebugRenderer.RENDER_JUMP;
//	}
//
//	private static AABB calculateWorldBox(Player player, Direction direction) {
//		Vec3 pos = player.position();
//		double halfWidth = player.getBbWidth() / 2;
//		double height = player.getBbHeight();
//
//		AABB baseBox = new AABB(
//			pos.x - halfWidth,
//			pos.y,
//			pos.z - halfWidth,
//			pos.x + halfWidth,
//			pos.y + height * FEET_BOX_MAX_HEIGHT_RATIO,
//			pos.z + halfWidth
//		).deflate(0.001);
//
//		double ox = direction.getStepX() * COLLISION_CHECK_DISTANCE;
//		double oy = direction.getStepY() * COLLISION_CHECK_DISTANCE;
//		double oz = direction.getStepZ() * COLLISION_CHECK_DISTANCE;
//
//		return baseBox.expandTowards(ox, oy, oz);
//	}
//}
