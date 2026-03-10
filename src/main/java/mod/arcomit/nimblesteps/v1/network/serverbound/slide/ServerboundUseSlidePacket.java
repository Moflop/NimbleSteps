package mod.arcomit.nimblesteps.v1.network.serverbound.slide;

import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.v2.content.context.GroundMovementData;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
//import mod.arcomit.nimblesteps.event.skills.SlideHandler;
import mod.arcomit.nimblesteps.v2.content.logic.SlideLogic;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 使用滑铲或闪避网络包。
 *
 * @author Arcomit
 * @since 2025-12-22
 */
public record ServerboundUseSlidePacket(float forwardImpulse, float leftImpulse) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundUseSlidePacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("slide_and_evade"));
	public static final StreamCodec<ByteBuf, ServerboundUseSlidePacket> STREAM_CODEC = StreamCodec.of(
		(buf, packet) -> {
			buf.writeFloat(packet.forwardImpulse);
			buf.writeFloat(packet.leftImpulse);
		},
		(buf) -> new ServerboundUseSlidePacket(buf.readFloat(), buf.readFloat())
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundUseSlidePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				MovementStateContext stateContext = MovementStateContext.get(player);
				GroundMovementData groundData = stateContext.getGroundData();
				if (SlideLogic.cannotStartSlide(player, groundData)) {
					return;
				}

				SlideLogic.performSlide(player, groundData, packet.forwardImpulse, packet.leftImpulse);
				// 避免触发服务端反作弊回拉
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundUseSlidePacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}