package mod.arcomit.nimblesteps.v1.init;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
import mod.arcomit.nimblesteps.v2.core.statemachine.MovementStateMachine;
import net.minecraft.world.entity.player.Player;
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
		DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, NimbleStepsMod.MODID);

	public static final Supplier<AttachmentType<MovementStateContext>> MOVEMENT_STATE_CONTEXT =
		ATTACHMENT_TYPES.register("movement_state_context", () ->
			AttachmentType.builder(MovementStateContext::new)
				.serialize(MovementStateContext.CODEC)
				.sync(MovementStateContext.STREAM_CODEC)
				.build()
	);

	public static final Supplier<AttachmentType<MovementStateMachine>> MOVEMENT_STATE_MACHINE =
		ATTACHMENT_TYPES.register("movement_state_machine", () ->
			AttachmentType.builder(holder -> {
				if (holder instanceof Player player) {
					return new MovementStateMachine(player);
				}
				return null;
			}).build()
		);

	public static void register(IEventBus bus) {
		ATTACHMENT_TYPES.register(bus);
	}
}
