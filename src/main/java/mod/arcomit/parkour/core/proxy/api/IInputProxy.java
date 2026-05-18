package mod.arcomit.parkour.core.proxy.api;

import net.minecraft.world.entity.player.Player;

public interface IInputProxy {
	float getLeftImpulse(Player player);
	float getForwardImpulse(Player player);
	boolean getUp(Player player);
	boolean getDown(Player player);
	boolean getLeft(Player player);
	boolean getRight(Player player);
	boolean getJumping(Player player);
	boolean getShiftKeyDown(Player player);
}