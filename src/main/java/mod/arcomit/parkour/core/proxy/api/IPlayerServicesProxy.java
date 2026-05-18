package mod.arcomit.parkour.core.proxy.api;

import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-28
 */
public interface IPlayerServicesProxy {

	void sendPosition(Player player);

	boolean isMoving(Player player);
}
