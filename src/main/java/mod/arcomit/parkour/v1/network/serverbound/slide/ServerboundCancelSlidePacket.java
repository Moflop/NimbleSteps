package mod.arcomit.parkour.v1.network.serverbound.slide;

import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
//import mod.arcomit.nimblesteps.event.skills.SlideHandler;
//import mod.arcomit.parkour.v2.content.behavior.slide.SlideLogic;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 取消滑铲网络包。
 *
 * @author Arcomit
 * @since 2025-12-22
 */
public class ServerboundCancelSlidePacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundCancelSlidePacket> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("cancel_slide"));
	public static final StreamCodec<ByteBuf, ServerboundCancelSlidePacket> STREAM_CODEC = StreamCodec.unit(new ServerboundCancelSlidePacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundCancelSlidePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				GroundData groundData = ParkourContext.get(player).groundData();
				//SlideLogic.cancelSlide(groundData);
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundCancelSlidePacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}