package mod.arcomit.parkour.v2.core.animation;

import com.zigythebird.playeranimcore.animation.RawAnimation;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import mod.arcomit.parkour.ParkourMod;
import net.minecraft.resources.ResourceLocation;

public enum ParkourAnim {
	SLIDE_1("slide_1"),
	SLIDE_2("slide_2");

	public final ResourceLocation id;

	ParkourAnim(String animName) {
		this.id = ParkourMod.prefix(animName);
	}
}