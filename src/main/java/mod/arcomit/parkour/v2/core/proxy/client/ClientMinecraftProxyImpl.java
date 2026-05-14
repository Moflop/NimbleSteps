package mod.arcomit.parkour.v2.core.proxy.client;

import mod.arcomit.parkour.v2.core.proxy.api.IMinecraftProxy;
import net.minecraft.client.Minecraft;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-28
 */
public class ClientMinecraftProxyImpl implements IMinecraftProxy {
	@Override
	public boolean isFirstPerson() {
		return Minecraft.getInstance().options.getCameraType().isFirstPerson();
	}
}
