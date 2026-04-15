package mod.arcomit.parkour.v2.core.animation.player.network;

import mod.arcomit.parkour.ParkourMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestPlayActionC2SPayload(ResourceLocation actionId, boolean interruptible) implements CustomPacketPayload {
	public static final Type<RequestPlayActionC2SPayload> TYPE = new Type<>(ParkourMod.prefix("request_play_action"));

	public static final StreamCodec<FriendlyByteBuf, RequestPlayActionC2SPayload> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, RequestPlayActionC2SPayload::actionId,
		ByteBufCodecs.BOOL, RequestPlayActionC2SPayload::interruptible,
		RequestPlayActionC2SPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() { return TYPE; }

	public static void handle(RequestPlayActionC2SPayload packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				// 服务端收到请求后，直接把这个动作广播给周围所有能看到该玩家的客户端
				PacketDistributor.sendToPlayersTrackingEntity(player,
					new BroadcastPlayActionS2CPayload(player.getId(), packet.actionId(), packet.interruptible())
				);
			}
		});
	}
}