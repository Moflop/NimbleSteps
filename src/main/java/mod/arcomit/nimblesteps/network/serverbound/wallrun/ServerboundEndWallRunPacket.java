package mod.arcomit.nimblesteps.network.serverbound.wallrun;

import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.event.skills.refactoring.WallRunHandler;
import mod.arcomit.nimblesteps.init.NsAttachmentTypes;
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
	public static final CustomPacketPayload.Type<ServerboundEndWallRunPacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("end_wall_run"));
	public static final StreamCodec<ByteBuf, ServerboundEndWallRunPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundEndWallRunPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundEndWallRunPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
				WallRunHandler.endWallRun(state);
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