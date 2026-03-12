package mod.arcomit.parkour.v1.network.serverbound.swimmingboost;

import mod.arcomit.parkour.ParkourMod;
import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
//import mod.arcomit.nimblesteps.event.skills.SwimmingBoostHandler;
import mod.arcomit.parkour.v2.core.context.SwimMovementData;
import mod.arcomit.parkour.v2.content.action.swimmingboost.SwimmingBoostLogic;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 水中推进网络包。
 *
 * @author Arcomit
 * @since 2025-12-22
 */
public class ServerboundUseSwimmingBoostPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundUseSwimmingBoostPacket> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("swimming_boost"));
	public static final StreamCodec<ByteBuf, ServerboundUseSwimmingBoostPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundUseSwimmingBoostPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundUseSwimmingBoostPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				SwimMovementData swimData = MovementStateContext.get(player).getSwimData();
				if (!SwimmingBoostLogic.canSwimmingBoost(player, swimData)) {
					return;
				}

				SwimmingBoostLogic.useSwimmingBoost(player, swimData);

				// 避免触发服务端反作弊回拉
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundUseSwimmingBoostPacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
