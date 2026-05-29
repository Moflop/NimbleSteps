package mod.arcomit.parkour.content.action.swimmingboost.network;

import mod.arcomit.parkour.ParkourMod;
import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.content.action.swimmingboost.SwimmingBoostSound;
import mod.arcomit.parkour.core.context.ParkourContext;
//import mod.arcomit.nimblesteps.event.skills.SwimmingBoostHandler;
import mod.arcomit.parkour.core.context.SwimData;
import mod.arcomit.parkour.content.action.swimmingboost.SwimmingBoostAction;
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
public class UseSwimmingBoostC2SPayload implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<UseSwimmingBoostC2SPayload> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("swimming_boost"));
	public static final StreamCodec<ByteBuf, UseSwimmingBoostC2SPayload> STREAM_CODEC = StreamCodec.unit(new UseSwimmingBoostC2SPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UseSwimmingBoostC2SPayload packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				SwimData swimData = ParkourContext.get(player).swimData();
				if (SwimmingBoostAction.execute(player, swimData)) {
					SwimmingBoostSound.play(player);
				}

				// 避免触发服务端反作弊回拉
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof UseSwimmingBoostC2SPayload;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
