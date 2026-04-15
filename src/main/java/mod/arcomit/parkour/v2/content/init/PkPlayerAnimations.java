package mod.arcomit.parkour.v2.content.init;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.animation.player.PlayerAnimation;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-14
 */
public class PkPlayerAnimations {
	public static final PlayerAnimation EMPTY_ANIM = new PlayerAnimation(ParkourMod.prefix("empty"));

	public static final PlayerAnimation SLIDE_1 = new PlayerAnimation(ParkourMod.prefix("slide_1"));

	public static final PlayerAnimation SLIDE_2 = new PlayerAnimation(ParkourMod.prefix("slide_2"));

	public static final PlayerAnimation LANDING_ROLL = new PlayerAnimation(ParkourMod.prefix("landing_roll"));

}
