package mod.arcomit.nimblesteps.network.serverbound.slide;

import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.event.skills.refactoring.SlideHandler;
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
	public static final CustomPacketPayload.Type<ServerboundCancelSlidePacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("cancel_slide"));
	public static final StreamCodec<ByteBuf, ServerboundCancelSlidePacket> STREAM_CODEC = StreamCodec.unit(new ServerboundCancelSlidePacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundCancelSlidePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				NimbleStepsState state = NimbleStepsState.getNimbleState(player);
				SlideHandler.cancelSlide(player, state);
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