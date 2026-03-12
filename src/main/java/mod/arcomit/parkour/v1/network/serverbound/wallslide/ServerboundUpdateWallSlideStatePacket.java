package mod.arcomit.parkour.v1.network.serverbound.wallslide;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
//import mod.arcomit.nimblesteps.event.skills.WallSlideHandler;
import mod.arcomit.parkour.v2.core.context.WallMovementData;
import mod.arcomit.parkour.v2.content.behavior.slide.WallSlideLogic;
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
	public static final Type<ServerboundUpdateWallSlideStatePacket> TYPE = new Type<>(ParkourMod.prefix("wall_slide"));
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
				MovementStateContext state = MovementStateContext.get(player);
				WallMovementData wallData = state.getWallData();

				if (shouldWallSlide) {
					if (WallSlideLogic.isValid(player, state)) {
						WallSlideLogic.startWallSliding(wallData);
					} else {
						// 客户端发来非法请求，直接否定
						WallSlideLogic.endWallSliding(wallData);
					}
				} else {
					WallSlideLogic.endWallSliding(wallData);
				}
			}
		});
	}
}
