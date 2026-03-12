package mod.arcomit.parkour.v1.network;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v1.network.clientbound.ClientboundUpdateCrawlStatePacket;
import mod.arcomit.parkour.v1.network.serverbound.crawl.ServerboundUpdateCrawlStatePacket;
import mod.arcomit.parkour.v1.network.serverbound.jump.ServerboundUseSwimmingJumpPacket;
import mod.arcomit.parkour.v1.network.serverbound.jump.ServerboundSupportWallJumpPacket;
import mod.arcomit.parkour.v1.network.serverbound.jump.ServerboundMountPacket;
import mod.arcomit.parkour.v1.network.serverbound.jump.ServerboundWallJumpPacket;
import mod.arcomit.parkour.v1.network.serverbound.roll.ServerboundSetLandingRollWindowPacket;
import mod.arcomit.parkour.v1.network.serverbound.slide.ServerboundCancelSlidePacket;
import mod.arcomit.parkour.v1.network.serverbound.slide.ServerboundUseSlidePacket;
import mod.arcomit.parkour.v1.network.serverbound.armhang.ServerboundSyncArmHangingDirectionPacket;
import mod.arcomit.parkour.v1.network.serverbound.swimmingboost.ServerboundUseSwimmingBoostPacket;
import mod.arcomit.parkour.v1.network.serverbound.wallclimb.ServerboundEndWallClimbPacket;
import mod.arcomit.parkour.v1.network.serverbound.wallclimb.ServerboundStartWallClimbPacket;
import mod.arcomit.parkour.v1.network.serverbound.wallrun.ServerboundClampWallRunDurationPacket;
import mod.arcomit.parkour.v1.network.serverbound.wallrun.ServerboundEndWallRunPacket;
import mod.arcomit.parkour.v1.network.serverbound.wallrun.ServerboundStartWallRunPacket;
import mod.arcomit.parkour.v1.network.serverbound.wallslide.ServerboundUpdateWallSlideStatePacket;
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
			ServerboundWallJumpPacket.TYPE,
			ServerboundWallJumpPacket.STREAM_CODEC,
			ServerboundWallJumpPacket::handle
		);
		registrar.playToServer(
			ServerboundSupportWallJumpPacket.TYPE,
			ServerboundSupportWallJumpPacket.STREAM_CODEC,
			ServerboundSupportWallJumpPacket::handle
		);
		registrar.playToServer(
			ServerboundMountPacket.TYPE,
			ServerboundMountPacket.STREAM_CODEC,
			ServerboundMountPacket::handle
		);
		registrar.playToServer(
			ServerboundUseSwimmingJumpPacket.TYPE,
			ServerboundUseSwimmingJumpPacket.STREAM_CODEC,
			ServerboundUseSwimmingJumpPacket::handle
		);
		registrar.playToServer(
			ServerboundSyncArmHangingDirectionPacket.TYPE,
			ServerboundSyncArmHangingDirectionPacket.STREAM_CODEC,
			ServerboundSyncArmHangingDirectionPacket::handle
		);

	}
}
