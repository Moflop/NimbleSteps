package mod.arcomit.parkour.v2.content.behavior.crawl;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.network.serverbound.crawl.ServerboundUpdateCrawlStatePacket;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.core.context.GroundMovementData;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
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
		ParkourStateMachine stateMachine = ParkourStateMachine.get(player);
		return stateMachine.isDefaultState()
			&& isValid(player);
	}

	public static boolean isValid(Player player) {
		return ServerConfig.enableCrawl
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isSwimming()
			&& PlayerStateUtils.isAbleToAction(player);
	}
}
