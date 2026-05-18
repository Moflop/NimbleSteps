package mod.arcomit.parkour.core.proxy.dummy;

import mod.arcomit.parkour.core.proxy.api.ICameraAnimProxy;
import net.minecraft.resources.ResourceLocation;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-28
 */
public class ServerDummyCameraAnimProxy implements ICameraAnimProxy {
	@Override
	public void playAnimation(ResourceLocation animationId) {}

	@Override
	public void stopAnimation() {}
}
