package mod.arcomit.parkour.v2.content.behavior.wallslide.network;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BroadcastWallSlideDirS2CPayload(int entityId, int direction) implements CustomPacketPayload {
	public static final Type<BroadcastWallSlideDirS2CPayload> TYPE = new Type<>(ParkourMod.prefix("broadcast_wall_slide_dir_s2c"));

	public static final StreamCodec<FriendlyByteBuf, BroadcastWallSlideDirS2CPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, BroadcastWallSlideDirS2CPayload::entityId,
		ByteBufCodecs.INT, BroadcastWallSlideDirS2CPayload::direction,
		BroadcastWallSlideDirS2CPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() { return TYPE; }

	public static void handle(BroadcastWallSlideDirS2CPayload packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientLevel level = Minecraft.getInstance().level;
			if (level != null) {
				Entity entity = level.getEntity(packet.entityId());
				// 只需给其他人(RemotePlayer)更新方向。LocalPlayer 自己在本地早就更新了。
				if (entity instanceof Player player && !player.isLocalPlayer()) {
					ParkourContext.get(player).wallData().setWallSlideDirection(packet.direction());
				}
			}
		});
	}
}