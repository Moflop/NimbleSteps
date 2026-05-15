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
// * 垂挂抓握点碰撞检测器。
// *
// * @author Arcomit
// */
//public final class ArmhangGripSensor {
//	private static final double COLLISION_CHECK_DISTANCE = 0.5;
//
//	public static SensorResult check(Player player, Direction direction, boolean bottomHeightFromEyeLevel) {
//		String variant = bottomHeightFromEyeLevel ? "eye" : "top";
//		SensorKey key = new SensorKey(SensorCategory.ARMHANG_GRIP, direction, variant);
//		return SensorCache.getOrCompute(player, key, () -> {
//			AABB box = calculateWorldBox(player, direction, bottomHeightFromEyeLevel);
//			boolean colliding = CollisionUtils.isBlockCollision(player.level(), box, ParkourTags.Blocks.SCAFFOLDING_BLOCKS);
//			return new SensorResult(box, colliding);
//		});
//	}
//
//	public static boolean shouldDebugRender(Player player, Direction direction) {
//		return SensorDebugRenderer.RENDER_ARMHANG && player.getDirection() == direction;
//	}
//
//	private static AABB calculateWorldBox(Player player, Direction direction, boolean bottomHeightFromEyeLevel) {
//		Vec3 pos = player.position();
//		double halfWidth = player.getBbWidth() / 2;
//		double height = player.getBbHeight();
//		double bottomHeight = bottomHeightFromEyeLevel ? player.getEyeHeight() : height;
//		double topOffset = height - player.getEyeHeight();
//
//		AABB baseBox = new AABB(
//			pos.x - halfWidth,
//			pos.y + bottomHeight,
//			pos.z - halfWidth,
//			pos.x + halfWidth,
//			pos.y + bottomHeight + topOffset,
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
