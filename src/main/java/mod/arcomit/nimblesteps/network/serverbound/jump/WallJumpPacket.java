package mod.arcomit.nimblesteps.network.serverbound.jump;

import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.event.skills.WallJumpHandler;
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
public class WallJumpPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<WallJumpPacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("wall_jump"));
	public static final StreamCodec<ByteBuf, WallJumpPacket> STREAM_CODEC = StreamCodec.unit(new WallJumpPacket());

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(WallJumpPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				NimbleStepsState state = NimbleStepsState.getNimbleState(player);
				WallJumpHandler.performWallJump(player, state);
				// 避免触发服务端反作弊回拉
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof WallJumpPacket;
	}

	@Override
	public int hashCode() {
		return TYPE.hashCode();
	}
}
