package mod.arcomit.parkour.core.proxy.client;

import mod.arcomit.parkour.core.client.animation.camera.CameraAnimationManager;
import mod.arcomit.parkour.core.proxy.api.ICameraAnimProxy;
import net.minecraft.resources.ResourceLocation;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-28
 */
public class ClientCameraAnimProxyImpl implements ICameraAnimProxy {
	@Override
	public void playAnimation(ResourceLocation animationId) {
		CameraAnimationManager.INSTANCE.play(animationId);
	}

	@Override
	public void stopAnimation() {
		CameraAnimationManager.INSTANCE.stop();
	}
}
