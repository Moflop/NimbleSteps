package mod.arcomit.parkour.content.behavior.landingroll.client;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.core.proxy.ParkourProxies;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-29
 */
public class ClientLandingRollAnimation {
	public static void playCameraAndPlayerAnim(Player player) {
		if (!player.isLocalPlayer()) return;
		boolean isFirstPerson = ParkourProxies.MINECRAFT_PROXY.isFirstPerson();
		if (isFirstPerson) {
			player.setXRot(0);// 第一人称时重置玩家的俯仰角，使玩家在抬头时翻滚结束也能朝前看，手感更好
		}
		// 播放摄像机翻滚动画
		ResourceLocation cameraAnimId = ResourceLocation.fromNamespaceAndPath("parkour", "camera_animations/landing_roll.json/landing_roll");
		ParkourProxies.CAMERA_PROXY.playAnimation(cameraAnimId);

		// 播放玩家翻滚动画
		ResourceLocation playerAnimId = ParkourMod.prefix("landing_roll");
		ParkourProxies.PLAYER_ANIM_PROXY.playOneOffAnimation(
			player, playerAnimId, false
		);
	}
}
