package mod.arcomit.nimblesteps.network.serverbound.wallrun;

import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.event.skills.WallRunHandler;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 开始墙跑网络包。
 *
 * @author Arcomit
 * @since 2025-12-27
 */
public class ServerboundStartWallRunPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundStartWallRunPacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("start_wall_run"));
	public static final StreamCodec<ByteBuf, ServerboundStartWallRunPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundStartWallRunPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundStartWallRunPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				NimbleStepsState state = NimbleStepsState.getNimbleState(player);
				if (WallRunHandler.canWallRun(player, state)) {
					WallRunHandler.startWallRun(state);
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundStartWallRunPacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}