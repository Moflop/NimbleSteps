package mod.arcomit.nimblesteps.network.serverbound.crawl;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.event.skills.CrawlHandler;
import mod.arcomit.nimblesteps.init.NsAttachmentTypes;
import mod.arcomit.nimblesteps.network.clientbound.ClientboundUpdateCrawlStatePacket;
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
 * @since 2025-12-21
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
			if (!(context.player() instanceof ServerPlayer player)) {
				return;
			}
			NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
			boolean shouldCrawl = packet.shouldCrawl();
			if (!shouldCrawl) {
				CrawlHandler.setCrawling(player, false);
				return;
			}

			// 玩家请求开始爬行 -> 需要校验条件
			if (CrawlHandler.canCrawl(player, state)) {
				CrawlHandler.setCrawling(player, true);
			} else {
				CrawlHandler.setCrawling(player, false);
				// 请求非法（作弊），强制同步客户端状态回“站立”
				PacketDistributor.sendToPlayersTrackingEntity(
					player, new ClientboundUpdateCrawlStatePacket(player.getId(), false));
			}
		});
	}
}
