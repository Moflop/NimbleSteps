package mod.arcomit.parkour.v2.core.animation.camera;

import java.util.Map;
import java.util.TreeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * 摄像机动画播放器。
 * 管理当前正在播放的动画状态和时间轴。
 */
public class CameraAnimationController {
	public static final CameraAnimationController INSTANCE = new CameraAnimationController();

	private static final float[] EMPTY_FRAME = new float[]{0.0f, 0.0f, 0.0f};

	private CameraAnimation currentAnimation = null;
	private boolean isPlaying = false;
	private long lastUpdateTimeMs = 0;
	private float currentAnimationTime = 0.0f;

	private CameraAnimationController() {}

	/**
	 * 播放指定的动画。
	 *
	 * @param animationId 动画的唯一资源标识符
	 */
	public void play(ResourceLocation animationId) {
		CameraAnimation animation = CameraAnimationRegistry.INSTANCE.getAnimation(animationId);
		if (animation != null) {
			this.currentAnimation = animation;
			this.isPlaying = true;
			this.currentAnimationTime = 0.0f;
			this.lastUpdateTimeMs = System.currentTimeMillis();
		} else {
			System.err.println("尝试播放不存在的摄像机动画: " + animationId);
		}
	}

	/**
	 * 停止当前动画。
	 */
	public void stop() {
		this.isPlaying = false;
		this.currentAnimation = null;
	}

	/**
	 * 每帧更新动画时间轴。应在渲染事件或 Tick 事件中调用。
	 */
	public void tick() {
		if (!isPlaying || currentAnimation == null) {
			return;
		}

		long now = System.currentTimeMillis();
		long deltaMs = now - lastUpdateTimeMs;

		// 无论是否暂停，每次都必须把 lastUpdateTimeMs 更新为现在，
		// 这样暂停期间度过的时间就会被“丢弃”，防止解除暂停时瞬间快进。
		this.lastUpdateTimeMs = now;

		// 检查 Minecraft 是否处于暂停状态
		if (!Minecraft.getInstance().isPaused()) {
			// 只有在没暂停时，才推进动画进度
			currentAnimationTime += deltaMs / 1000.0f;
		}

		// 检查动画是否播放完毕
		if (currentAnimationTime > currentAnimation.getLength()) {
			if (currentAnimation.isLoop()) {
				// 循环播放：减去一个周期的长度，这样能保证循环动画绝对不会掉帧或产生误差
				currentAnimationTime -= currentAnimation.getLength();
			} else {
				stop();
			}
		}
	}

	public float[] getCurrentRotation() {
		if (!isPlaying || currentAnimation == null) {
			return EMPTY_FRAME;
		}
		return interpolateFrames(currentAnimation.getRotations());
	}

	public float[] getCurrentPosition() {
		if (!isPlaying || currentAnimation == null) {
			return EMPTY_FRAME;
		}
		return interpolateFrames(currentAnimation.getPositions());
	}

	private float[] interpolateFrames(TreeMap<Float, float[]> frames) {
		if (frames.isEmpty()) {
			return EMPTY_FRAME;
		}

		Map.Entry<Float, float[]> floor = frames.floorEntry(currentAnimationTime);
		Map.Entry<Float, float[]> ceiling = frames.ceilingEntry(currentAnimationTime);

		if (floor == null) {
			return ceiling != null ? ceiling.getValue() : EMPTY_FRAME;
		}
		if (ceiling == null || floor.getKey().equals(ceiling.getKey())) {
			return floor.getValue();
		}

		float progress = (currentAnimationTime - floor.getKey()) / (ceiling.getKey() - floor.getKey());
		float[] start = floor.getValue();
		float[] end = ceiling.getValue();

		return new float[]{
			Mth.lerp(progress, start[0], end[0]),
			Mth.lerp(progress, start[1], end[1]),
			Mth.lerp(progress, start[2], end[2])
		};
	}

	public boolean isPlaying() {
		return isPlaying;
	}
}