package mod.arcomit.parkour.v2.core.proxy.dummy;

import mod.arcomit.parkour.v2.core.proxy.api.IInputProxy;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-22
 */
public class ServerDummyInputProxy implements IInputProxy {
	@Override
	public float getLeftImpulse(Player player) {
		return 0;
	}

	@Override
	public float getForwardImpulse(Player player) {
		return 0;
	}

	@Override
	public boolean getUp(Player player) {
		return false;
	}

	@Override
	public boolean getDown(Player player) {
		return false;
	}

	@Override
	public boolean getLeft(Player player) {
		return false;
	}

	@Override
	public boolean getRight(Player player) {
		return false;
	}

	@Override
	public boolean getJumping(Player player) {
		return false;
	}

	@Override
	public boolean getShiftKeyDown(Player player) {
		return false;
	}
}
