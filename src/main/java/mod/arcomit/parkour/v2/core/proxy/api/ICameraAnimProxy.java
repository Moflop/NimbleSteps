package mod.arcomit.parkour.v2.core.proxy.api;

import net.minecraft.resources.ResourceLocation;

public interface ICameraAnimProxy {
	/**
	 * 播放摄像机动画
	 * @param animationId 动画ID
	 */
	void playAnimation(ResourceLocation animationId);

	/**
	 * 停止当前的摄像机动画
	 */
	void stopAnimation();
}
