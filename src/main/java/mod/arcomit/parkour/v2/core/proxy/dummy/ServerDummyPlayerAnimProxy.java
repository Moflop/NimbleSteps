package mod.arcomit.parkour.v2.core.proxy.dummy;

import mod.arcomit.parkour.v2.core.proxy.api.IPlayerAnimProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-25
 */
public class ServerDummyPlayerAnimProxy implements IPlayerAnimProxy {
	@Override
	public void playStateAnimation(Player player) {}

	@Override
	public void playOneOffAnimation(Player player, ResourceLocation animId, boolean interruptible) {}
}
