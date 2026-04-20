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

public record BroadcastPlayActionS2CPayload(int entityId, ResourceLocation actionId, boolean interruptible) implements CustomPacketPayload {
	public static final Type<BroadcastPlayActionS2CPayload> TYPE = new Type<>(ParkourMod.prefix("broadcast_play_action"));

	public static final StreamCodec<FriendlyByteBuf, BroadcastPlayActionS2CPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, BroadcastPlayActionS2CPayload::entityId,
		ResourceLocation.STREAM_CODEC, BroadcastPlayActionS2CPayload::actionId,
		ByteBufCodecs.BOOL, BroadcastPlayActionS2CPayload::interruptible,
		BroadcastPlayActionS2CPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() { return TYPE; }

	public static void handle(BroadcastPlayActionS2CPayload packet, IPayloadContext context) {
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