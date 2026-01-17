package mod.arcomit.nimblesteps.init;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * 附加类型注册类。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
public class NsAttachmentTypes {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
		DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, NimbleStepsMod.MODID);

	public static final Supplier<AttachmentType<NimbleStepsState>> NIMBLE_STEPS_STATE = ATTACHMENT_TYPES.register(
		"crawl", () -> AttachmentType.serializable(NimbleStepsState::new)
			.sync(NimbleStepsState.STREAM_CODEC)
			.build()
	);

	public static void register(IEventBus bus) {
		ATTACHMENT_TYPES.register(bus);
	}
}
