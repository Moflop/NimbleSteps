package mod.arcomit.parkour.v2.content.init;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.sensor.SensorManager;
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
public class PkAttachmentTypes {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
		DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ParkourMod.MODID);

	public static final Supplier<AttachmentType<ParkourContext>> PARKOUR_CONTEXT =
		ATTACHMENT_TYPES.register("parkour_context", () ->
			AttachmentType.builder(ParkourContext::new)
				.serialize(ParkourContext.CODEC)
				.sync(ParkourContext.STREAM_CODEC)
				.build()
	);

	public static final Supplier<AttachmentType<SensorManager>> SENSOR_MANAGER =
		ATTACHMENT_TYPES.register("sensor_manager", () ->
			AttachmentType.builder(SensorManager::new)
				.build()
		);

	public static void register(IEventBus bus) {
		ATTACHMENT_TYPES.register(bus);
	}
}
