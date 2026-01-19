package mod.arcomit.nimblesteps.network.serverbound.jump;

import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.event.skills.MountHandler;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 支撑上墙网络包。
 *
 * @author Arcomit
 * @since 2026-01-18
 */
public class ServerboundMountPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundMountPacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("mount"));
	public static final StreamCodec<ByteBuf, ServerboundMountPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundMountPacket());

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundMountPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				NimbleStepsState state = NimbleStepsState.getNimbleState(player);
				player.connection.resetPosition();
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundMountPacket;
	}

	@Override
	public int hashCode() {
		return TYPE.hashCode();
	}
}
