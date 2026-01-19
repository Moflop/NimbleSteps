package mod.arcomit.nimblesteps.network.serverbound.wallslide;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.event.skills.WallSlideHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 墙滑状态更新网络包。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
public record ServerboundUpdateWallSlideStatePacket(boolean shouldWallSlide) implements CustomPacketPayload {
	public static final Type<ServerboundUpdateWallSlideStatePacket> TYPE = new Type<>(NimbleStepsMod.prefix("wall_slide"));
	public static final StreamCodec<FriendlyByteBuf, ServerboundUpdateWallSlideStatePacket> STREAM_CODEC = StreamCodec.composite(
		StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean),
		ServerboundUpdateWallSlideStatePacket::shouldWallSlide,
		ServerboundUpdateWallSlideStatePacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundUpdateWallSlideStatePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				boolean shouldWallSlide = packet.shouldWallSlide();
				NimbleStepsState state = NimbleStepsState.getNimbleState(player);
				if (shouldWallSlide) {
					if (WallSlideHandler.canWallSlide(player, state)) {
						WallSlideHandler.startWallSliding(state);
					} else {
						WallSlideHandler.startWallSliding(state);
						// 请求非法（作弊），强制同步客户端状态
						NimbleStepsState.setNimbleState(player, state);
					}
				}else {
					state.setWallSliding(false);
				}

			}
		});
	}
}
