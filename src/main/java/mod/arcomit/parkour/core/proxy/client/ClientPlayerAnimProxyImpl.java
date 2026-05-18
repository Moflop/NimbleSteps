package mod.arcomit.parkour.core.proxy.client;

import mod.arcomit.parkour.core.client.animation.player.PlayerAnimationManager;
import mod.arcomit.parkour.core.proxy.api.IPlayerAnimProxy;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-25
 */
@OnlyIn(Dist.CLIENT)
public class ClientPlayerAnimProxyImpl implements IPlayerAnimProxy {

	@Override
	public void playStateAnimation(Player player) {
		if (player instanceof AbstractClientPlayer clientPlayer) {
			PlayerAnimationManager.INSTANCE.playStateAnimation(clientPlayer);
		}
	}

	@Override
	public void playOneOffAnimation(Player player, ResourceLocation animId, boolean interruptible) {
		if (player instanceof AbstractClientPlayer clientPlayer) {
			PlayerAnimationManager.INSTANCE.playOneOffAnimation(
				clientPlayer, animId, interruptible
			);
		}
	}

}
