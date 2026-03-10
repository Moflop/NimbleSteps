package mod.arcomit.nimblesteps.v2.content.client.handler;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.v2.content.client.NsKeyBindings;
import mod.arcomit.nimblesteps.v2.content.client.NsKeyMapping;
import mod.arcomit.nimblesteps.v2.content.client.event.InputJustPressedEvent;
import mod.arcomit.nimblesteps.v1.utils.PlayerStateUtils;
import mod.arcomit.nimblesteps.v2.content.context.GroundMovementData;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
import mod.arcomit.nimblesteps.v2.content.logic.CrawlLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = NimbleStepsMod.MODID, value = Dist.CLIENT)
public class ToggleCrawlHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void tryToggleCrawlOnInput(InputJustPressedEvent event) {
		NsKeyMapping key = event.getKeyMapping();
		if (key != NsKeyBindings.SLIDE_KEY) return;

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;

		MovementStateContext context = MovementStateContext.get(player);
		GroundMovementData groundData = context.getGroundData();
		boolean isCrawling = groundData.isCrawling();

		// 如果已经在爬行，直接退出
		if (isCrawling) {
			CrawlLogic.performCrawlToggle(groundData, false);
			return;
		}

		// 如果没有在爬行，需要没有进入状态且静止不动
		if (CrawlLogic.canStartCrawl(player) && !PlayerStateUtils.isPlayerMoving(player)) {
			CrawlLogic.performCrawlToggle(groundData, true);
		}
	}

}