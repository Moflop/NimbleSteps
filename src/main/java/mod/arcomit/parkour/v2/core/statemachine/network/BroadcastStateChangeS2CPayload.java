package mod.arcomit.parkour.v2.core.statemachine.network;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.init.PkRegistries;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 广播玩家状态改变给其他追踪该实体的客户端
 *
 * @author Arcomit
 * @since 2026-03-14
 */
public record BroadcastStateChangeS2CPayload(int entityId, ResourceLocation stateId, int animVariant) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<BroadcastStateChangeS2CPayload> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("broadcast_state_change"));

	public static final StreamCodec<FriendlyByteBuf, BroadcastStateChangeS2CPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, BroadcastStateChangeS2CPayload::entityId,
		ResourceLocation.STREAM_CODEC, BroadcastStateChangeS2CPayload::stateId,
		ByteBufCodecs.INT, BroadcastStateChangeS2CPayload::animVariant,
		BroadcastStateChangeS2CPayload::new
	);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(BroadcastStateChangeS2CPayload packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			// 客户端逻辑：找到对应的玩家实体并更新状态
			ClientLevel level = Minecraft.getInstance().level;
			if (level != null) {
				Entity entity = level.getEntity(packet.entityId());
				// 本地玩家(LocalPlayer)已经通过预测切换了状态，只处理远程玩家(RemotePlayer)
				if (entity != null && entity instanceof Player player && !player.isLocalPlayer()) {
					IParkourState newState = PkRegistries.PARKOUR_STATE_REGISTRY.get(packet.stateId());
					if (newState != null) {
						ParkourStateMachine.transitionTo(player, newState, packet.animVariant());
					}
				}
			}
		});
	}
}