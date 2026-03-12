package mod.arcomit.parkour.v1.init;

import mod.arcomit.parkour.ParkourMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 音效注册类。
 *
 * @author Arcomit
 * @since 2026-01-05
 */
public class NsSounds {
	public static final DeferredRegister<SoundEvent> SOUNDS =
		DeferredRegister.create(Registries.SOUND_EVENT, ParkourMod.MODID);

	public static final Supplier<SoundEvent> SLIDE = SOUNDS.register("slide",
		() -> SoundEvent.createVariableRangeEvent(ParkourMod.prefix("slide")));

	public static final Supplier<SoundEvent> LANDING_ROLL = SOUNDS.register("landing_roll",
		() -> SoundEvent.createVariableRangeEvent(ParkourMod.prefix("landing_roll")));

	public static final Supplier<SoundEvent> WALL_JUMP = SOUNDS.register("wall_jump",
		() -> SoundEvent.createVariableRangeEvent(ParkourMod.prefix("wall_jump")));

	public static void register(IEventBus bus) {
		SOUNDS.register(bus);
	}
}
