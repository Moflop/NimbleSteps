package mod.arcomit.parkour.v2.core.animation.player.v2;

import mod.arcomit.parkour.ParkourMod;
import net.minecraft.resources.ResourceLocation;

public class ParkourAnimation {

	public final ResourceLocation id;

	public ParkourAnimation(ResourceLocation id) {
		this.id = id;
	}

	// 常态动画
	public static final ParkourAnimation SLIDE_1 = new ParkourAnimation(ParkourMod.prefix("slide_1"));
	public static final ParkourAnimation SLIDE_2 = new ParkourAnimation(ParkourMod.prefix("slide_2"));

	// 新增：一次性动作动画示例（例如着陆翻滚、受击、主动攀爬发力等）
	public static final ParkourAnimation LANDING_ROLL = new ParkourAnimation(ParkourMod.prefix("landing_roll"));
	public static final ParkourAnimation HEAVY_IMPACT = new ParkourAnimation(ParkourMod.prefix("heavy_impact"));
}