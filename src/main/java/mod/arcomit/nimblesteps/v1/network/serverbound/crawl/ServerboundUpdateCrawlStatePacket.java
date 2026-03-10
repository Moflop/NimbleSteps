package mod.arcomit.nimblesteps.v1.network.serverbound.crawl;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.v1.network.clientbound.ClientboundUpdateCrawlStatePacket;
import mod.arcomit.nimblesteps.v2.content.context.GroundMovementData;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
import mod.arcomit.nimblesteps.v2.content.state.CrawlState;
import mod.arcomit.nimblesteps.v2.core.statemachine.MovementStateMachine;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 发给服务端的爬行状态更新网络包。
 *
 * @author Arcomit
 */
public record ServerboundUpdateCrawlStatePacket(boolean shouldCrawl) implements CustomPacketPayload {
	public static final Type<ServerboundUpdateCrawlStatePacket> TYPE = new Type<>(NimbleStepsMod.prefix("server_crawl"));
	public static final StreamCodec<FriendlyByteBuf, ServerboundUpdateCrawlStatePacket> STREAM_CODEC = StreamCodec.composite(
		StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean),
		ServerboundUpdateCrawlStatePacket::shouldCrawl,
		ServerboundUpdateCrawlStatePacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundUpdateCrawlStatePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof ServerPlayer player)) return;
			GroundMovementData groundData = MovementStateContext.get(player).getGroundData();
			MovementStateMachine stateMachine = MovementStateMachine.get(player);
			boolean shouldCrawl = packet.shouldCrawl();

			if (!shouldCrawl || CrawlState.isValid(player)) {
				// 服务端批准，修改上下文
				groundData.setCrawling(shouldCrawl);
			} else {
				// 非法请求，驳回并在上下文里写 false
				groundData.setCrawling(false);
				PacketDistributor.sendToPlayersTrackingEntityAndSelf(
					player, new ClientboundUpdateCrawlStatePacket(player.getId(), false));
			}
		});
	}
}