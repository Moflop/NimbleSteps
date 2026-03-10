package mod.arcomit.nimblesteps.v1.network.serverbound.armhang;

import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-02-18
 */
public record ServerboundSyncArmHangingDirectionPacket(int newDirection3DDataValue) implements CustomPacketPayload  {
	public static final CustomPacketPayload.Type<ServerboundSyncArmHangingDirectionPacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("sync_armhanging_direction"));
	public static final StreamCodec<ByteBuf, ServerboundSyncArmHangingDirectionPacket> STREAM_CODEC = StreamCodec.of(
		(buf, packet) -> {
			buf.writeInt(packet.newDirection3DDataValue);
		},
		(buf) -> new ServerboundSyncArmHangingDirectionPacket(buf.readInt())
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundSyncArmHangingDirectionPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				MovementStateContext state = MovementStateContext.get(player);
				//state.setArmHangingDirection(packet.newDirection3DDataValue);
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundSyncArmHangingDirectionPacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}