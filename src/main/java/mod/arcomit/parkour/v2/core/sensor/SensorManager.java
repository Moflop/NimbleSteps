//package mod.arcomit.parkour.v2.core.sensor;
//
//import mod.arcomit.parkour.v2.core.sensor.impl.*;
//import net.minecraft.core.Direction;
//import net.minecraft.world.entity.player.Player;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.function.Function;
//import java.util.function.Predicate;
//
///**
// * Sensor 静态注册中心，供 Debug 渲染器遍历所有 Sensor。
// *
// * @author Arcomit
// */
//public final class SensorManager {
//
//	public record SensorDef(SensorKey key, Function<Player, SensorResult> check, Predicate<Player> shouldRender) {}
//
//	private static final List<SensorDef> ALL;
//
//	static {
//		List<SensorDef> list = new ArrayList<>();
//		for (Direction dir : Direction.Plane.HORIZONTAL) {
//			list.add(new SensorDef(new SensorKey(SensorCategory.HEAD_WALL, dir),
//				p -> HeadWallSensor.check(p, dir),
//				HeadWallSensor::shouldDebugRender));
//
//			list.add(new SensorDef(new SensorKey(SensorCategory.FEET_WALL, dir),
//				p -> FeetWallSensor.check(p, dir),
//				FeetWallSensor::shouldDebugRender));
//
//			list.add(new SensorDef(new SensorKey(SensorCategory.JUMP_WALL, dir),
//				p -> JumpWallSensor.check(p, dir),
//				JumpWallSensor::shouldDebugRender));
//
//			list.add(new SensorDef(new SensorKey(SensorCategory.ARMHANG_GRIP, dir, "eye"),
//				p -> ArmhangGripSensor.check(p, dir, true),
//				p -> ArmhangGripSensor.shouldDebugRender(p, dir)));
//
//			list.add(new SensorDef(new SensorKey(SensorCategory.ARMHANG_GRIP, dir, "top"),
//				p -> ArmhangGripSensor.check(p, dir, false),
//				p -> ArmhangGripSensor.shouldDebugRender(p, dir)));
//
//			list.add(new SensorDef(new SensorKey(SensorCategory.ARMHANG_SUPPORT, dir, "eye"),
//				p -> ArmhangSupportSensor.check(p, dir, true),
//				p -> ArmhangSupportSensor.shouldDebugRender(p, dir)));
//
//			list.add(new SensorDef(new SensorKey(SensorCategory.ARMHANG_SUPPORT, dir, "top"),
//				p -> ArmhangSupportSensor.check(p, dir, false),
//				p -> ArmhangSupportSensor.shouldDebugRender(p, dir)));
//		}
//		ALL = Collections.unmodifiableList(list);
//	}
//
//	public static List<SensorDef> getAll() {
//		return ALL;
//	}
//}
