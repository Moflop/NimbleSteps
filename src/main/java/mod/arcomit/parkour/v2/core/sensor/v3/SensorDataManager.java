package mod.arcomit.parkour.v2.core.sensor.v3;

import mod.arcomit.parkour.v2.content.init.ParkourAttachmentTypes;
import net.minecraft.world.entity.player.Player;

import java.util.EnumMap;
import java.util.Map;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-15
 */
public class SensorDataManager {
	private final Map<SensorType, SensorData> sensorDataMap = new EnumMap<>(SensorType.class);

	public SensorDataManager() {
		for (SensorType sensorType : SensorType.values()) {
			sensorDataMap.put(sensorType, new SensorData());
		}
	}

	public SensorData getData(SensorType sensorType) {
		return sensorDataMap.get(sensorType);
	}

	public static void set(Player player, SensorDataManager dataManager) {
		player.setData(ParkourAttachmentTypes.SENSOR_DATA_MANAGER, dataManager);
	}

	public static SensorDataManager get(Player player) {
		return player.getData(ParkourAttachmentTypes.SENSOR_DATA_MANAGER);
	}
}
