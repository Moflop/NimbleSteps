package mod.arcomit.nimblesteps.network.serverbound.wallclimb;

import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.event.skills.WallClimbHandler;
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
public class ServerboundStartWallClimbPacket implements CustomPacketPayload {
	public static final Type<ServerboundStartWallClimbPacket> TYPE = new Type<>(NimbleStepsMod.prefix("start_wall_climb"));
	public static final StreamCodec<ByteBuf, ServerboundStartWallClimbPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundStartWallClimbPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundStartWallClimbPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				NimbleStepsState state = NimbleStepsState.getNimbleState(player);
				if (WallClimbHandler.canWallClimb(player, state)) {
					WallClimbHandler.startWallClimb(state);
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundStartWallClimbPacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}