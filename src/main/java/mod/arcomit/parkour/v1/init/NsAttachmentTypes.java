package mod.arcomit.parkour.v1.init;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
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
		DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ParkourMod.MODID);

	public static final Supplier<AttachmentType<MovementStateContext>> MOVEMENT_STATE_CONTEXT =
		ATTACHMENT_TYPES.register("movement_state_context", () ->
			AttachmentType.builder(MovementStateContext::new)
				.serialize(MovementStateContext.CODEC)
				.sync(MovementStateContext.STREAM_CODEC)
				.build()
	);

	public static final Supplier<AttachmentType<ParkourStateMachine>> MOVEMENT_STATE_MACHINE =
		ATTACHMENT_TYPES.register("movement_state_machine", () ->
			AttachmentType.builder(ParkourStateMachine::new).build()
		);

	public static void register(IEventBus bus) {
		ATTACHMENT_TYPES.register(bus);
	}
}
