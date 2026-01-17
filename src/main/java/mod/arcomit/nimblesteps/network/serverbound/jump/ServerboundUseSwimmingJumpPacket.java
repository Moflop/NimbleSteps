package mod.arcomit.nimblesteps.network.serverbound.jump;

import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.event.skills.refactoring.FreestyleHandler;
import mod.arcomit.nimblesteps.event.skills.refactoring.SwimmingImprovementsHandler;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 自由泳跳跃网络包。
 *
 * @author Arcomit
 * @since 2025-12-23
 */
public class ServerboundUseSwimmingJumpPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundUseSwimmingJumpPacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("freestyle_jump"));
	public static final StreamCodec<ByteBuf, ServerboundUseSwimmingJumpPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundUseSwimmingJumpPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundUseSwimmingJumpPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				NimbleStepsState state = NimbleStepsState.getNimbleState(player);
				if (!FreestyleHandler.canFreestyle(player, state)) {
					return;
				}
				SwimmingImprovementsHandler.applySwimmingJumpMovement(player);
				// 避免触发服务端反作弊回拉
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundUseSwimmingJumpPacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}