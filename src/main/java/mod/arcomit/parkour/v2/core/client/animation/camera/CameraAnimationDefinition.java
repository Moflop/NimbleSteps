package mod.arcomit.parkour.v2.core.client.animation.camera;

import com.google.gson.annotations.SerializedName;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;

/**
 * 映射 Blockbench 导出的原始摄像机动画 JSON 结构。
 */
@OnlyIn(Dist.CLIENT)
public class CameraAnimationDefinition {
	@SerializedName("animation_length")
	public float animationLength;

	@SerializedName("loop")
	public String loop;

	@SerializedName("bones")
	public Bones bones;

	public static class Bones {
		@SerializedName("camera")
		public Camera camera;
	}

	public static class Camera {
		@SerializedName("rotation")
		public Map<String, float[]> rotation;

		@SerializedName("position")
		public Map<String, float[]> position;
	}
}