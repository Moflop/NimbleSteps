package mod.arcomit.parkour.v2.content.behavior.landingroll.client;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.init.ParkourSounds;
import mod.arcomit.parkour.v2.core.proxy.ParkourProxies;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-22
 */
public class ClientLandingRollEffect {
	private static final float ROLL_SOUND_VOLUME = 1.0f;
	private static final float ROLL_SOUND_PITCH = 1.0f;

	public static void playSound(Player player) {
		ParkourProxies.SOUND_PROXY.playEntityBoundSound(
			ParkourSounds.LANDING_ROLL.get(),
			SoundSource.PLAYERS,
			ROLL_SOUND_VOLUME,
			ROLL_SOUND_PITCH,
			player,
			player.getRandom().nextLong()
		);
	}

	public static void playAnimation(Player player) {
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
