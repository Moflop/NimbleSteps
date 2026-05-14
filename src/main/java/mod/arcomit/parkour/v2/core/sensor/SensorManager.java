package mod.arcomit.parkour.v2.core.sensor;

import mod.arcomit.parkour.v2.content.init.ParkourAttachmentTypes;
import mod.arcomit.parkour.v2.core.sensor.impl.*;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SensorManager {
	private final Map<String, ISensor> sensors = new HashMap<>();

	public SensorManager() {
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			this.register(new HeadWallSensor(dir)); // 头部传感器
			this.register(new FeetWallSensor(dir)); // 脚部传感器

			this.register(new JumpWallSensor(dir)); // 墙跳传感器

			this.register(new ArmhangGripSensor(dir, true)); // 基于眼睛高度的抓握点传感器
			this.register(new ArmhangSupportSensor(dir, true)); // 基于眼睛高度的支撑点传感器

			this.register(new ArmhangGripSensor(dir, false)); // 基于碰撞箱顶端高度的抓握点传感器
			this.register(new ArmhangSupportSensor(dir, false)); // 基于碰撞箱顶端高度的支撑点传感器
		}
	}

	// 注册传感器
	public void register(ISensor sensor) {
		sensors.put(sensor.getId(), sensor);
	}

	// 获取指定的传感器
	public ISensor getSensor(String id) {
		return sensors.get(id);
	}

	// 获取所有传感器（给 Debug 渲染器遍历用）
	public Collection<ISensor> getAllSensors() {
		return sensors.values();
	}

	public static SensorManager get(Player player) {
		return player.getData(ParkourAttachmentTypes.SENSOR_MANAGER);
	}
}