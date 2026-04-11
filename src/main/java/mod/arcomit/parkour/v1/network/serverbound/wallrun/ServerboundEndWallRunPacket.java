package mod.arcomit.parkour.v1.network.serverbound.wallrun;

import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
//import mod.arcomit.nimblesteps.event.skills.WallRunHandler;
import mod.arcomit.parkour.v2.content.init.PkAttachmentTypes;
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
public class ServerboundEndWallRunPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundEndWallRunPacket> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("end_wall_run"));
	public static final StreamCodec<ByteBuf, ServerboundEndWallRunPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundEndWallRunPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundEndWallRunPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				ParkourContext state = player.getData(PkAttachmentTypes.PARKOUR_CONTEXT);
//				WallRunHandler.endWallRun(state);
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundEndWallRunPacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}