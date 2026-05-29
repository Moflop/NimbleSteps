package mod.arcomit.parkour.content.handler;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.content.action.swimmingjump.network.UseSwimmingJumpC2SPayload;
import mod.arcomit.parkour.content.action.supportwalljump.network.SupportWallJumpC2SPayload;
import mod.arcomit.parkour.content.action.walljump.network.WallJumpC2SPayload;
import mod.arcomit.parkour.content.behavior.landingroll.network.ServerboundSetLandingRollWindowPacket;
import mod.arcomit.parkour.content.behavior.armhang.network.ServerboundSyncArmHangingDirectionPacket;
import mod.arcomit.parkour.content.action.swimmingboost.network.UseSwimmingBoostC2SPayload;
import mod.arcomit.parkour.content.behavior.wallslide.network.BroadcastWallSlideDirS2CPayload;
import mod.arcomit.parkour.core.client.animation.player.network.BroadcastPlayOneOffAnimS2CPayload;
import mod.arcomit.parkour.core.client.animation.player.network.RequestPlayOneOffAnimC2SPayload;
import mod.arcomit.parkour.core.statemachine.network.BroadcastStateChangeS2CPayload;
import mod.arcomit.parkour.core.statemachine.network.RequestStateTransitionC2SPayload;
import mod.arcomit.parkour.core.statemachine.network.ForceLocalPlayerStateS2CPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 注册网络通信数据包处理器。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class RegisterPayloadHandler {

	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");

		registrar.playToServer(
			ServerboundSetLandingRollWindowPacket.TYPE,
			ServerboundSetLandingRollWindowPacket.STREAM_CODEC,
			ServerboundSetLandingRollWindowPacket::handle
		);

		registrar.playToServer(
			UseSwimmingBoostC2SPayload.TYPE,
			UseSwimmingBoostC2SPayload.STREAM_CODEC,
			UseSwimmingBoostC2SPayload::handle
		);

		registrar.playToServer(
			WallJumpC2SPayload.TYPE,
			WallJumpC2SPayload.STREAM_CODEC,
			WallJumpC2SPayload::handle
		);
		registrar.playToServer(
			SupportWallJumpC2SPayload.TYPE,
			SupportWallJumpC2SPayload.STREAM_CODEC,
			SupportWallJumpC2SPayload::handle
		);
		registrar.playToServer(
			UseSwimmingJumpC2SPayload.TYPE,
			UseSwimmingJumpC2SPayload.STREAM_CODEC,
			UseSwimmingJumpC2SPayload::handle
		);
		registrar.playToServer(
			ServerboundSyncArmHangingDirectionPacket.TYPE,
			ServerboundSyncArmHangingDirectionPacket.STREAM_CODEC,
			ServerboundSyncArmHangingDirectionPacket::handle
		);

		// ==================V2架构========================
		registrar.playToServer(
			RequestStateTransitionC2SPayload.TYPE,
			RequestStateTransitionC2SPayload.STREAM_CODEC,
			RequestStateTransitionC2SPayload::handle
		);
		registrar.playToClient(
			ForceLocalPlayerStateS2CPayload.TYPE,
			ForceLocalPlayerStateS2CPayload.STREAM_CODEC,
			ForceLocalPlayerStateS2CPayload::handle
		);
		registrar.playToClient(
			BroadcastStateChangeS2CPayload.TYPE,
			BroadcastStateChangeS2CPayload.STREAM_CODEC,
			BroadcastStateChangeS2CPayload::handle
		);

		registrar.playToServer(
			RequestPlayOneOffAnimC2SPayload.TYPE,
			RequestPlayOneOffAnimC2SPayload.STREAM_CODEC,
			RequestPlayOneOffAnimC2SPayload::handle
		);
		registrar.playToClient(
			BroadcastPlayOneOffAnimS2CPayload.TYPE,
			BroadcastPlayOneOffAnimS2CPayload.STREAM_CODEC,
			BroadcastPlayOneOffAnimS2CPayload::handle
		);

		registrar.playToClient(
			BroadcastWallSlideDirS2CPayload.TYPE,
			BroadcastWallSlideDirS2CPayload.STREAM_CODEC,
			BroadcastWallSlideDirS2CPayload::handle
		);
	}
}
