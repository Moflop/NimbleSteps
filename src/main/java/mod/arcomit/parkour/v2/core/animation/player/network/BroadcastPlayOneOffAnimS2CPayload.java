package mod.arcomit.parkour.v2.core.animation.player.network;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.animation.player.PlayerAnimationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BroadcastPlayOneOffAnimS2CPayload(int entityId, ResourceLocation actionId, boolean interruptible) implements CustomPacketPayload {
	public static final Type<BroadcastPlayOneOffAnimS2CPayload> TYPE = new Type<>(ParkourMod.prefix("broadcast_play_action"));

	public static final StreamCodec<FriendlyByteBuf, BroadcastPlayOneOffAnimS2CPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, BroadcastPlayOneOffAnimS2CPayload::entityId,
		ResourceLocation.STREAM_CODEC, BroadcastPlayOneOffAnimS2CPayload::actionId,
		ByteBufCodecs.BOOL, BroadcastPlayOneOffAnimS2CPayload::interruptible,
		BroadcastPlayOneOffAnimS2CPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() { return TYPE; }

	public static void handle(BroadcastPlayOneOffAnimS2CPayload packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientLevel level = Minecraft.getInstance().level;
			if (level != null) {
				Entity entity = level.getEntity(packet.entityId());
				// 确保实体是客户端玩家，并且不是本地玩家自己（因为自己早就预测播放过了）
				if (entity instanceof AbstractClientPlayer player && !player.isLocalPlayer()) {
					PlayerAnimationManager.playOneOffAnimation(player, packet.actionId(), packet.interruptible());
				}
			}
		});
	}
}