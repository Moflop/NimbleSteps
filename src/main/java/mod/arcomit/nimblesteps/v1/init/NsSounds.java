package mod.arcomit.nimblesteps.v1.init;

import mod.arcomit.nimblesteps.NimbleStepsMod;
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
		DeferredRegister.create(Registries.SOUND_EVENT, NimbleStepsMod.MODID);

	public static final Supplier<SoundEvent> SLIDE = SOUNDS.register("slide",
		() -> SoundEvent.createVariableRangeEvent(NimbleStepsMod.prefix("slide")));

	public static final Supplier<SoundEvent> LANDING_ROLL = SOUNDS.register("landing_roll",
		() -> SoundEvent.createVariableRangeEvent(NimbleStepsMod.prefix("landing_roll")));

	public static final Supplier<SoundEvent> WALL_JUMP = SOUNDS.register("wall_jump",
		() -> SoundEvent.createVariableRangeEvent(NimbleStepsMod.prefix("wall_jump")));

	public static void register(IEventBus bus) {
		SOUNDS.register(bus);
	}
}
