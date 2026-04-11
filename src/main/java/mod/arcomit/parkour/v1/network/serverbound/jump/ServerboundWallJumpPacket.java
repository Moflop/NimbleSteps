package mod.arcomit.parkour.v1.network.serverbound.jump;

import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
//import mod.arcomit.nimblesteps.event.skills.WallJumpHandler;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 墙跳网络包。
 *
 * @author Arcomit
 * @since 2026-01-01
 */
public class ServerboundWallJumpPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundWallJumpPacket> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("wall_jump"));
	public static final StreamCodec<ByteBuf, ServerboundWallJumpPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundWallJumpPacket());

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundWallJumpPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				ParkourContext state = ParkourContext.get(player);
//				WallJumpHandler.performWallJump(player, state);
				// 避免触发服务端反作弊回拉
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundWallJumpPacket;
	}

	@Override
	public int hashCode() {
		return TYPE.hashCode();
	}
}
