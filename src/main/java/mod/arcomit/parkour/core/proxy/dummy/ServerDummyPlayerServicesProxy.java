package mod.arcomit.parkour.core.proxy.dummy;

import mod.arcomit.parkour.core.proxy.api.IPlayerServicesProxy;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-28
 */
public class ServerDummyPlayerServicesProxy implements IPlayerServicesProxy {
	@Override
	public void sendPosition(Player player) {}

	@Override
	public boolean isMoving(Player player) {
		return false;
	}
}
