package mod.arcomit.parkour.v1.network.serverbound.wallclimb;

import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
//import mod.arcomit.nimblesteps.event.skills.WallClimbHandler;
import mod.arcomit.parkour.v1.init.NsAttachmentTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 结束墙跑网络包。
 *
 * @author Arcomit
 * @since 2025-12-27
 */
public class ServerboundEndWallClimbPacket implements CustomPacketPayload {
	public static final Type<ServerboundEndWallClimbPacket> TYPE = new Type<>(ParkourMod.prefix("end_wall_climb"));
	public static final StreamCodec<ByteBuf, ServerboundEndWallClimbPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundEndWallClimbPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundEndWallClimbPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				MovementStateContext state = player.getData(NsAttachmentTypes.MOVEMENT_STATE_CONTEXT);
//				WallClimbHandler.endWallClimb(state);
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundEndWallClimbPacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}