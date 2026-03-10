package mod.arcomit.nimblesteps.v1.network.clientbound;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 发给客户端的爬行状态更新网络包。
 *
 * @author Arcomit
 */
public record ClientboundUpdateCrawlStatePacket(int entityId, boolean crawlState) implements CustomPacketPayload {
	public static final Type<ClientboundUpdateCrawlStatePacket> TYPE = new Type<>(NimbleStepsMod.prefix("client_crawl"));
	public static final StreamCodec<FriendlyByteBuf, ClientboundUpdateCrawlStatePacket> STREAM_CODEC = StreamCodec.composite(
		StreamCodec.of(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt),
		ClientboundUpdateCrawlStatePacket::entityId,
		StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean),
		ClientboundUpdateCrawlStatePacket::crawlState,
		ClientboundUpdateCrawlStatePacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ClientboundUpdateCrawlStatePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.flow() == PacketFlow.CLIENTBOUND) {
				Level level = Minecraft.getInstance().level;
				if (level == null) return;
				Entity entity = level.getEntity(packet.entityId());
				if (entity instanceof Player player) {
					// 客户端收到服务端的同步，只需覆写上下文，本地状态机自然会纠正
					MovementStateContext.get(player).getGroundData().setCrawling(packet.crawlState());
				}
			}
		});
	}
}