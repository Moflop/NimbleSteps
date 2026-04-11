package mod.arcomit.parkour.v2.core.statemachine.network;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.init.PkRegistries;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-02-18
 */
public record RequestStateTransitionC2SPayload(ResourceLocation targetStateId, int animVariant) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<RequestStateTransitionC2SPayload> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("request_state_transition"));

	public static final StreamCodec<FriendlyByteBuf, RequestStateTransitionC2SPayload> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, RequestStateTransitionC2SPayload::targetStateId,
		ByteBufCodecs.INT, RequestStateTransitionC2SPayload::animVariant,
		RequestStateTransitionC2SPayload::new
	);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(RequestStateTransitionC2SPayload packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				ResourceLocation stateId = packet.targetStateId();
				int animVariant = packet.animVariant();
				IParkourState targetState = PkRegistries.PARKOUR_REGISTRY.get(stateId);

				if (targetState != null && targetState.canEnter(player)) {
					ParkourStateMachine.transitionTo(player, targetState, animVariant);

					// 发送给所有能看到该玩家的客户端（通常不需要发给玩家自己，因为客户端已经提前预测了状态）
					PacketDistributor.sendToPlayersTrackingEntity(player,
						new BroadcastStateChangeS2CPayload(player.getId(), stateId, animVariant)
					);
				} else {
					// 获取当前服务端合法状态的ID
					StateData stateData = ParkourContext.get(player).stateData();
					IParkourState currentState = stateData.getState();
					ResourceLocation currentStateId = PkRegistries.PARKOUR_REGISTRY.getKey(currentState);

					// 向客户端发包RejectStateRequestS2CPayload拒绝请求并把状态还原
					if (currentStateId != null) {
						PacketDistributor.sendToPlayer(player,
							new SyncLocalPlayerStateS2CPayload(currentStateId)
						);
					}
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RequestStateTransitionC2SPayload;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}