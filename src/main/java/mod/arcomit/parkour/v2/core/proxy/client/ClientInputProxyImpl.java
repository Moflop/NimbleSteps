package mod.arcomit.parkour.v2.core.proxy.client;

import mod.arcomit.parkour.v2.core.proxy.api.IInputProxy;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-22
 */
@OnlyIn(Dist.CLIENT)
public class ClientInputProxyImpl implements IInputProxy {

	@Override
	public float getLeftImpulse(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			return localPlayer.input.leftImpulse;
		}
		return 0;
	}

	@Override
	public float getForwardImpulse(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			return localPlayer.input.forwardImpulse;
		}
		return 0;
	}

	@Override
	public boolean getUp(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			return localPlayer.input.up;
		}
		return false;
	}

	@Override
	public boolean getDown(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			return localPlayer.input.down;
		}
		return false;
	}

	@Override
	public boolean getLeft(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			return localPlayer.input.left;
		}
		return false;
	}

	@Override
	public boolean getRight(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			return localPlayer.input.right;
		}
		return false;
	}

	@Override
	public boolean getJumping(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			return localPlayer.input.jumping;
		}
		return false;
	}

	@Override
	public boolean getShiftKeyDown(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			return localPlayer.input.shiftKeyDown;
		}
		return false;
	}
}
