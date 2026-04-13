package mod.arcomit.parkour.v2.core.animation.camera;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * 摄像机动画注册表与加载器。
 * 负责从资源包中扫描并加载所有 .json 后缀的动画文件。
 */
public class CameraAnimationRegistry implements ResourceManagerReloadListener {
	public static final CameraAnimationRegistry INSTANCE = new CameraAnimationRegistry();
	private static final Gson GSON = new Gson();

	// 存放动画的目录，例如：assets/parkour/camera_animations
	private static final String ANIMATION_DIRECTORY = "camera_animations";
	private final Map<ResourceLocation, CameraAnimation> animations = new HashMap<>();

	private CameraAnimationRegistry() {}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		animations.clear();
		Map<ResourceLocation, Resource> resources = resourceManager.listResources(
			ANIMATION_DIRECTORY,
			path -> path.getPath().endsWith(".json")
		);

		for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
			ResourceLocation fileLocation = entry.getKey();
			try (BufferedReader reader = entry.getValue().openAsReader()) {
				JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

				if (!root.has("animations")) {
					continue;
				}

				JsonObject animationsObject = root.getAsJsonObject("animations");
				for (String animationName : animationsObject.keySet()) {
					CameraAnimationDefinition definition = GSON.fromJson(
						animationsObject.get(animationName),
						CameraAnimationDefinition.class
					);

					// 生成唯一标识符，格式为 "modid:camera_animations/文件名/动画名"
					String validPath = fileLocation.getPath() + "/" + animationName.toLowerCase();
					ResourceLocation animationId = ResourceLocation.fromNamespaceAndPath(
						fileLocation.getNamespace(),
						validPath
					);

					animations.put(animationId, new CameraAnimation(animationId, definition));
				}
			} catch (Exception e) {
				System.err.println("加载摄像机动画失败: " + fileLocation);
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据 ID 获取动画。
	 */
	public CameraAnimation getAnimation(ResourceLocation id) {
		return animations.get(id);
	}
}