package mod.arcomit.parkour.v2.core.sensor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SensorManager {
	private final Map<String, AbstractBoxSensor> sensors = new HashMap<>();

	// 注册传感器
	public void register(AbstractBoxSensor sensor) {
		sensors.put(sensor.getId(), sensor);
	}

	// 获取指定的传感器
	public AbstractBoxSensor get(String id) {
		return sensors.get(id);
	}

	// 获取所有传感器（给 Debug 渲染器遍历用）
	public Collection<AbstractBoxSensor> getAllSensors() {
		return sensors.values();
	}
}