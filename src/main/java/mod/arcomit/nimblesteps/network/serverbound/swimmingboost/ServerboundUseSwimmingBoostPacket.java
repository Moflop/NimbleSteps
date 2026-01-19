package mod.arcomit.nimblesteps.network.serverbound.swimmingboost;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import io.netty.buffer.ByteBuf;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.event.skills.SwimmingBoostHandler;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 水中推进网络包。
 *
 * @author Arcomit
 * @since 2025-12-22
 */
public class ServerboundUseSwimmingBoostPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundUseSwimmingBoostPacket> TYPE = new CustomPacketPayload.Type<>(NimbleStepsMod.prefix("swimming_boost"));
	public static final StreamCodec<ByteBuf, ServerboundUseSwimmingBoostPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundUseSwimmingBoostPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundUseSwimmingBoostPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				NimbleStepsState state = NimbleStepsState.getNimbleState(player);
				if (!SwimmingBoostHandler.canSwimmingBoost(player, state)) {
					return;
				}

				SwimmingBoostHandler.useSwimmingBoost(player, state);
				player.level().playSound(
					player,
					player.getX(),
					player.getY(),
					player.getZ(),
					SoundEvents.AMBIENT_UNDERWATER_ENTER,
					SoundSource.PLAYERS,
					SwimmingBoostHandler.SWIMMING_BOOST_SOUND_VOLUME,
					SwimmingBoostHandler.SWIMMING_BOOST_SOUND_PITCH);

				// 避免触发服务端反作弊回拉
				if (player.connection != null) {
					player.connection.resetPosition();
				}
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundUseSwimmingBoostPacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
