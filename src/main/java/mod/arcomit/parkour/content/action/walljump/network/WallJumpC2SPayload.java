package mod.arcomit.parkour.content.action.walljump.network;

import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.content.action.walljump.WallJumpAction;
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
public record WallJumpC2SPayload() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<WallJumpC2SPayload> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("wall_jump"));
	public static final StreamCodec<ByteBuf, WallJumpC2SPayload> STREAM_CODEC = StreamCodec.unit(new WallJumpC2SPayload());

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(WallJumpC2SPayload packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				WallJumpAction.execute(player);
				// 避免触发服务端反作弊回拉
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}
}
