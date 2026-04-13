package mod.arcomit.parkour.v2.core.animation.player;

import mod.arcomit.parkour.ParkourMod;
import net.minecraft.resources.ResourceLocation;

public enum PlayerAnimmation {
	SLIDE_1("slide_1"),
	SLIDE_2("slide_2"),
	LANDING_ROLL("landing_roll");

	public final ResourceLocation id;

	PlayerAnimmation(String animName) {
		this.id = ParkourMod.prefix(animName);
	}
}