package mod.arcomit.parkour.v2.core.proxy.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface IPlayerAnimProxy {
	void playStateAnimation(Player player);

	void playOneOffAnimation(Player player, ResourceLocation animId, boolean interruptible);
}
