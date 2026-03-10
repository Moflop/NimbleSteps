package mod.arcomit.nimblesteps.v2.content.logic;

import mod.arcomit.nimblesteps.v1.network.serverbound.crawl.ServerboundUpdateCrawlStatePacket;
import mod.arcomit.nimblesteps.v2.content.context.GroundMovementData;
import mod.arcomit.nimblesteps.v2.content.state.CrawlState;
import mod.arcomit.nimblesteps.v2.core.statemachine.MovementStateMachine;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-03-10
 */
public class CrawlLogic {
	public static void performCrawlToggle(GroundMovementData groundData, boolean targetCrawlState) {
		groundData.setCrawling(targetCrawlState);
		PacketDistributor.sendToServer(new ServerboundUpdateCrawlStatePacket(targetCrawlState));
	}

	public static boolean canStartCrawl(Player player) {
		MovementStateMachine stateMachine = MovementStateMachine.get(player);
		return stateMachine.isDefaultState()
			&& CrawlState.isValid(player);
	}
}
