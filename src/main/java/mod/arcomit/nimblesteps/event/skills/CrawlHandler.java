package mod.arcomit.nimblesteps.event.skills;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.client.NsKeyBindings;
import mod.arcomit.nimblesteps.client.NsKeyMapping;
import mod.arcomit.nimblesteps.client.event.InputJustPressedEvent;
import mod.arcomit.nimblesteps.network.clientbound.ClientboundUpdateCrawlStatePacket;
import mod.arcomit.nimblesteps.network.serverbound.crawl.ServerboundUpdateCrawlStatePacket;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 爬行处理器。
 *
 * @author Arcomit
 * @since 2026-01-05
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class CrawlHandler {

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void tryToggleCrawlOnInput(InputJustPressedEvent event) {
		NsKeyMapping key = event.getKeyMapping();
		if (key != NsKeyBindings.SLIDE_KEY) {
			return;
		}

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		// 只要在爬行状态下，就允许通过按键直接退出爬行状态
		if (state.isCrawling()) {
			performCrawlToggle(player, state);
			return;
		}

		// 检查是否能够进入爬行状态
		if (!canStartCraw(player, state)) {
			return;
		}

		performCrawlToggle(player, state);
	}

	@SubscribeEvent
	public static void checkCrawlStateValidity(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if (player.level().isClientSide) {
			return;
		}

		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (!state.isCrawling()) {
			return;
		}

		if (canCrawl(player, state)) {
			return;
		}

		CrawlHandler.setCrawling(player, false);
		PacketDistributor.sendToPlayersTrackingEntity(player,
			new ClientboundUpdateCrawlStatePacket(player.getId(), false));
	}

	private static void performCrawlToggle(Player player, NimbleStepsState state) {
		boolean toggledCrawlingState = !state.isCrawling();
		CrawlHandler.setCrawling(player, toggledCrawlingState);
		PacketDistributor.sendToServer(new ServerboundUpdateCrawlStatePacket(toggledCrawlingState));
	}

	public static void setCrawling(Player player , boolean crawling) {
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		state.setCrawling(crawling);
		player.setForcedPose(crawling ? Pose.SWIMMING : null);
	}


	private static boolean canStartCraw(LocalPlayer player, NimbleStepsState state) {
		return !PlayerStateUtils.isPlayerMoving(player) && canCrawl(player, state);
	}

	public static boolean canCrawl(Player player, NimbleStepsState state) {
		return ServerConfig.enableCrawl
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isSwimming()
			&& !state.isArmHanging()
			&& !state.isSliding()
			&& !state.isWallSliding()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}
