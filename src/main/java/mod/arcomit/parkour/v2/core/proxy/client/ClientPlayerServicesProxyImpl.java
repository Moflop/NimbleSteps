package mod.arcomit.parkour.v2.core.proxy.client;

import mod.arcomit.parkour.v2.core.proxy.api.IPlayerServicesProxy;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-28
 */
public class ClientPlayerServicesProxyImpl implements IPlayerServicesProxy {
	@Override
	public void sendPosition(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			localPlayer.sendPosition();
		}
	}

	@Override
	public boolean isMoving(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			Input input = localPlayer.input;
			return input.forwardImpulse != 0 || input.leftImpulse != 0;
		}
		return false;
	}
}
