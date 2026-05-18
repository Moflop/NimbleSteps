package mod.arcomit.parkour.core.proxy.dummy;

import mod.arcomit.parkour.core.proxy.api.IMinecraftProxy;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-28
 */
public class ServerDummyMinecraftProxy implements IMinecraftProxy {
	@Override
	public boolean isFirstPerson() {
		return false;
	}
}
