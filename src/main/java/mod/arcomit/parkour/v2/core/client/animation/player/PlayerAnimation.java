package mod.arcomit.parkour.v2.core.client.animation.player;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerAnimation {

	public final ResourceLocation id;

	public PlayerAnimation(ResourceLocation id) {
		this.id = id;
	}
}