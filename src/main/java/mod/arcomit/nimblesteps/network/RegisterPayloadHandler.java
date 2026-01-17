package mod.arcomit.nimblesteps.network;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.network.clientbound.ClientboundUpdateCrawlStatePacket;
import mod.arcomit.nimblesteps.network.serverbound.crawl.ServerboundUpdateCrawlStatePacket;
import mod.arcomit.nimblesteps.network.serverbound.jump.ServerboundUseSwimmingJumpPacket;
import mod.arcomit.nimblesteps.network.serverbound.jump.SupportWallJumpPacket;
import mod.arcomit.nimblesteps.network.serverbound.jump.WallJumpPacket;
import mod.arcomit.nimblesteps.network.serverbound.roll.ServerboundSetLandingRollWindowPacket;
import mod.arcomit.nimblesteps.network.serverbound.slide.ServerboundCancelSlidePacket;
import mod.arcomit.nimblesteps.network.serverbound.slide.ServerboundUseSlidePacket;
import mod.arcomit.nimblesteps.network.serverbound.swimmingboost.ServerboundUseSwimmingBoostPacket;
import mod.arcomit.nimblesteps.network.serverbound.wallclimb.ServerboundEndWallClimbPacket;
import mod.arcomit.nimblesteps.network.serverbound.wallclimb.ServerboundStartWallClimbPacket;
import mod.arcomit.nimblesteps.network.serverbound.wallrun.ServerboundClampWallRunDurationPacket;
import mod.arcomit.nimblesteps.network.serverbound.wallrun.ServerboundEndWallRunPacket;
import mod.arcomit.nimblesteps.network.serverbound.wallrun.ServerboundStartWallRunPacket;
import mod.arcomit.nimblesteps.network.serverbound.wallslide.ServerboundUpdateWallSlideStatePacket;
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
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class RegisterPayloadHandler {

	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");
		registrar.playToClient(
			ClientboundUpdateCrawlStatePacket.TYPE,
			ClientboundUpdateCrawlStatePacket.STREAM_CODEC,
			ClientboundUpdateCrawlStatePacket::handle
		);
		registrar.playToServer(
			ServerboundUpdateCrawlStatePacket.TYPE,
			ServerboundUpdateCrawlStatePacket.STREAM_CODEC,
			ServerboundUpdateCrawlStatePacket::handle
		);

		registrar.playToServer(
			ServerboundUseSlidePacket.TYPE,
			ServerboundUseSlidePacket.STREAM_CODEC,
			ServerboundUseSlidePacket::handle
		);
		registrar.playToServer(
			ServerboundCancelSlidePacket.TYPE,
			ServerboundCancelSlidePacket.STREAM_CODEC,
			ServerboundCancelSlidePacket::handle
		);
		registrar.playToServer(
			ServerboundUpdateWallSlideStatePacket.TYPE,
			ServerboundUpdateWallSlideStatePacket.STREAM_CODEC,
			ServerboundUpdateWallSlideStatePacket::handle
		);

		registrar.playToServer(
			ServerboundSetLandingRollWindowPacket.TYPE,
			ServerboundSetLandingRollWindowPacket.STREAM_CODEC,
			ServerboundSetLandingRollWindowPacket::handle
		);

		registrar.playToServer(
			ServerboundUseSwimmingBoostPacket.TYPE,
			ServerboundUseSwimmingBoostPacket.STREAM_CODEC,
			ServerboundUseSwimmingBoostPacket::handle
		);

		registrar.playToServer(
			ServerboundStartWallRunPacket.TYPE,
			ServerboundStartWallRunPacket.STREAM_CODEC,
			ServerboundStartWallRunPacket::handle
		);
		registrar.playToServer(
			ServerboundEndWallRunPacket.TYPE,
			ServerboundEndWallRunPacket.STREAM_CODEC,
			ServerboundEndWallRunPacket::handle
		);
		registrar.playToServer(
			ServerboundClampWallRunDurationPacket.TYPE,
			ServerboundClampWallRunDurationPacket.STREAM_CODEC,
			ServerboundClampWallRunDurationPacket::handle
		);

		registrar.playToServer(
			ServerboundStartWallClimbPacket.TYPE,
			ServerboundStartWallClimbPacket.STREAM_CODEC,
			ServerboundStartWallClimbPacket::handle
		);
		registrar.playToServer(
			ServerboundEndWallClimbPacket.TYPE,
			ServerboundEndWallClimbPacket.STREAM_CODEC,
			ServerboundEndWallClimbPacket::handle
		);

		registrar.playToServer(
			WallJumpPacket.TYPE,
			WallJumpPacket.STREAM_CODEC,
			WallJumpPacket::handle
		);
		registrar.playToServer(
			SupportWallJumpPacket.TYPE,
			SupportWallJumpPacket.STREAM_CODEC,
			SupportWallJumpPacket::handle
		);
		registrar.playToServer(
			ServerboundUseSwimmingJumpPacket.TYPE,
			ServerboundUseSwimmingJumpPacket.STREAM_CODEC,
			ServerboundUseSwimmingJumpPacket::handle
		);
	}
}
