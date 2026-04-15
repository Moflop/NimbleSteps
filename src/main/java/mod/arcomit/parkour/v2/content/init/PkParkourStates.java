package mod.arcomit.parkour.v2.content.init;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.behavior.backstep.BackstepState;
import mod.arcomit.parkour.v2.content.behavior.base.DefaultState;
import mod.arcomit.parkour.v2.content.behavior.crawl.CrawlState;
import mod.arcomit.parkour.v2.content.behavior.landingroll.LandingRollState;
import mod.arcomit.parkour.v2.content.behavior.slide.SlideState;
import mod.arcomit.parkour.v2.content.behavior.wallslide.WallSlideState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-12
 */
public class PkParkourStates {
	private static final DeferredRegister<IParkourState> PARKOUR_STATES = DeferredRegister.create(PkRegistries.PARKOUR_REGISTRY, ParkourMod.MODID);

	public static final DeferredHolder<IParkourState, DefaultState> DEFAULT = PARKOUR_STATES.register("default", DefaultState::new);

	public static final DeferredHolder<IParkourState, CrawlState> CRAWL = PARKOUR_STATES.register("crawl", CrawlState::new);

	public static final DeferredHolder<IParkourState, SlideState> SLIDE = PARKOUR_STATES.register("slide", SlideState::new);

	public static final DeferredHolder<IParkourState, BackstepState> BACKSTEP = PARKOUR_STATES.register("backstep", BackstepState::new);

	public static final DeferredHolder<IParkourState, LandingRollState> LANDING_ROLL = PARKOUR_STATES.register("landing_roll", LandingRollState::new);

	public static final DeferredHolder<IParkourState, WallSlideState> WALL_SLIDE = PARKOUR_STATES.register("wall_slide", WallSlideState::new);

	public static void register(IEventBus modEventBus) {
		PARKOUR_STATES.register(modEventBus);
	}
}
