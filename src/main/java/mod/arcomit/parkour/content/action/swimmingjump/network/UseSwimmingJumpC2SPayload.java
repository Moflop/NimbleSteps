package mod.arcomit.parkour.content.action.swimmingjump.network;

import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.content.action.swimmingjump.SwimmingJumpAction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 自由泳跳跃网络包。
 *
 * @author Arcomit
 * @since 2025-12-23
 */
public class UseSwimmingJumpC2SPayload implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<UseSwimmingJumpC2SPayload> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("freestyle_jump"));
	public static final StreamCodec<ByteBuf, UseSwimmingJumpC2SPayload> STREAM_CODEC = StreamCodec.unit(new UseSwimmingJumpC2SPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UseSwimmingJumpC2SPayload packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				SwimmingJumpAction.execute(player);
				// 避免触发服务端反作弊回拉
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof UseSwimmingJumpC2SPayload;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}