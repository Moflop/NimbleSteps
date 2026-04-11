package mod.arcomit.parkour.v1.network.serverbound.roll;

import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 设置着陆翻滚窗口网络包。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
public class ServerboundSetLandingRollWindowPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundSetLandingRollWindowPacket> TYPE = new CustomPacketPayload.Type<>(ParkourMod.prefix("set_landing_roll_window"));
	public static final StreamCodec<ByteBuf, ServerboundSetLandingRollWindowPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundSetLandingRollWindowPacket());


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerboundSetLandingRollWindowPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				GroundData groundData = ParkourContext.get(player).groundData();
				groundData.setLandingRollWindow(ServerConfig.landingRollWindow);
			}
		});
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ServerboundSetLandingRollWindowPacket;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
