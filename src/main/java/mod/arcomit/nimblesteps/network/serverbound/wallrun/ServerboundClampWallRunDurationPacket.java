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
 * 截断墙跑持续时间网络包。
 *
 * @author Arcomit
 * @since 2026-01-10
 */

public class ServerboundClampWallRunDurationPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundClampWallRunDurationPacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("clamp_wall_run_duration"));
	public static final StreamCodec<ByteBuf, ServerboundClampWallRunDurationPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundClampWallRunDurationPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundClampWallRunDurationPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
				WallRunHandler.clampWallRunDuration(state);
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundClampWallRunDurationPacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}