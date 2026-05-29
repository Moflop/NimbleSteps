package mod.arcomit.parkour.content.action.supportwalljump.network;

import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.core.context.ParkourContext;
//import mod.arcomit.nimblesteps.event.skills.SupportWallJumpHandler;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 撑墙跳网络包。
 *
 * @author Arcomit
 * @since 2026-01-02
 */
public class SupportWallJumpC2SPayload implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SupportWallJumpC2SPayload> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("support_wall_jump"));
	public static final StreamCodec<ByteBuf, SupportWallJumpC2SPayload> STREAM_CODEC = StreamCodec.unit(new SupportWallJumpC2SPayload());

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SupportWallJumpC2SPayload packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				ParkourContext state = ParkourContext.get(player);
//				SupportWallJumpHandler.useSupportWallJump(player, state);
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SupportWallJumpC2SPayload;
	}

	@Override
	public int hashCode() {
		return TYPE.hashCode();
	}
}
