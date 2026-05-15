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
// * 垂挂支撑点碰撞检测器。
// *
// * @author Arcomit
// */
//public final class ArmhangSupportSensor {
//	private static final double COLLISION_CHECK_DISTANCE = 0.5;
//	private static final double BOTTOM_COLLISION_MIN_HEIGHT_RATIO = 0.34;
//
//	public static SensorResult check(Player player, Direction direction, boolean topHeightFromEyeLevel) {
//		String variant = topHeightFromEyeLevel ? "eye" : "top";
//		SensorKey key = new SensorKey(SensorCategory.ARMHANG_SUPPORT, direction, variant);
//		return SensorCache.getOrCompute(player, key, () -> {
//			AABB box = calculateWorldBox(player, direction, topHeightFromEyeLevel);
//			boolean colliding = CollisionUtils.isBlockCollision(player.level(), box, ParkourTags.Blocks.SCAFFOLDING_BLOCKS);
//			return new SensorResult(box, colliding);
//		});
//	}
//
//	public static boolean shouldDebugRender(Player player, Direction direction) {
//		return SensorDebugRenderer.RENDER_ARMHANG && player.getDirection() == direction;
//	}
//
//	private static AABB calculateWorldBox(Player player, Direction direction, boolean topHeightFromEyeLevel) {
//		Vec3 pos = player.position();
//		double halfWidth = player.getBbWidth() / 2;
//		double height = player.getBbHeight();
//		double topHeight = topHeightFromEyeLevel ? player.getEyeHeight() : height;
//		double bottomOffset = height * BOTTOM_COLLISION_MIN_HEIGHT_RATIO;
//
//		AABB baseBox = new AABB(
//			pos.x - halfWidth,
//			pos.y + topHeight - bottomOffset,
//			pos.z - halfWidth,
//			pos.x + halfWidth,
//			pos.y + topHeight,
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
