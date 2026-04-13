package mod.arcomit.parkour.v2.core.animation.camera;

import java.util.Map;
import java.util.TreeMap;
import net.minecraft.resources.ResourceLocation;

/**
 * 解析后的摄像机动画数据模型。
 * 在加载阶段提前将时间戳字符串解析为浮点数，以确保播放时的高性能。
 */
public class CameraAnimation {
	private final ResourceLocation id;
	private final float length;
	private final boolean loop;
	private final TreeMap<Float, float[]> rotations;
	private final TreeMap<Float, float[]> positions;

	public CameraAnimation(ResourceLocation id, CameraAnimationDefinition definition) {
		this.id = id;
		this.length = definition.animationLength;
		this.loop = "true".equalsIgnoreCase(definition.loop);
		this.rotations = new TreeMap<>();
		this.positions = new TreeMap<>();

		if (definition.bones != null && definition.bones.camera != null) {
			parseKeyframes(definition.bones.camera.rotation, this.rotations);
			parseKeyframes(definition.bones.camera.position, this.positions);
		}
	}

	private void parseKeyframes(Map<String, float[]> source, TreeMap<Float, float[]> target) {
		if (source == null) {
			return;
		}
		for (Map.Entry<String, float[]> entry : source.entrySet()) {
			try {
				float time = Float.parseFloat(entry.getKey());
				target.put(time, entry.getValue());
			} catch (NumberFormatException e) {
				// 忽略无效的时间戳
			}
		}
	}

	public ResourceLocation getId() {
		return id;
	}

	public float getLength() {
		return length;
	}

	public boolean isLoop() {
		return loop;
	}

	public TreeMap<Float, float[]> getRotations() {
		return rotations;
	}

	public TreeMap<Float, float[]> getPositions() {
		return positions;
	}
}