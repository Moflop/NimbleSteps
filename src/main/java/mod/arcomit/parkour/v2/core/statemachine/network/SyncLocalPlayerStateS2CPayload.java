package mod.arcomit.parkour.v2.core.statemachine.network;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.init.PkRegistries;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 拒绝客户端的状态转换请求，并通知其回滚到当前合法状态
 *
 * @author Arcomit
 * @since 2026-03-14
 */
public record SyncLocalPlayerStateS2CPayload(ResourceLocation correctStateId, int animVariant) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SyncLocalPlayerStateS2CPayload> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("sync_state_to_local"));

	public static final StreamCodec<FriendlyByteBuf, SyncLocalPlayerStateS2CPayload> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, SyncLocalPlayerStateS2CPayload::correctStateId,
		ByteBufCodecs.INT, SyncLocalPlayerStateS2CPayload::animVariant,
		SyncLocalPlayerStateS2CPayload::new
	);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SyncLocalPlayerStateS2CPayload packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			// 客户端逻辑：强制拉回服务端的合法状态
			Player player = Minecraft.getInstance().player;
			if (player != null) {
				IParkourState correctState = PkRegistries.PARKOUR_REGISTRY.get(packet.correctStateId());
				if (correctState != null) {
					ParkourStateMachine.transitionTo(player, correctState, packet.animVariant());
				}
			}
		});
	}
}