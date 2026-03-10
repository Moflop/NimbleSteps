package mod.arcomit.nimblesteps.v1.network.serverbound.jump;

import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
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
public class ServerboundSupportWallJumpPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundSupportWallJumpPacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("support_wall_jump"));
	public static final StreamCodec<ByteBuf, ServerboundSupportWallJumpPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundSupportWallJumpPacket());

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundSupportWallJumpPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				MovementStateContext state = MovementStateContext.get(player);
//				SupportWallJumpHandler.useSupportWallJump(player, state);
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundSupportWallJumpPacket;
	}

	@Override
	public int hashCode() {
		return TYPE.hashCode();
	}
}
