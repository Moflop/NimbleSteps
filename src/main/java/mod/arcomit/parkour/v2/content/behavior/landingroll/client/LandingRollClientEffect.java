package mod.arcomit.parkour.v2.content.behavior.landingroll.client;

import mod.arcomit.parkour.v2.content.init.PkPlayerAnimations;
import mod.arcomit.parkour.v2.content.init.PkSounds;
import mod.arcomit.parkour.v2.core.animation.camera.CameraAnimationController;
import mod.arcomit.parkour.v2.core.animation.player.PlayerAnimationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-22
 */
@OnlyIn(Dist.CLIENT)
public class LandingRollClientEffect {
	private static final float ROLL_SOUND_VOLUME = 1.0f;
	private static final float ROLL_SOUND_PITCH = 1.0f;

	public static void playSound(Player player) {
		Minecraft.getInstance().getSoundManager().play(
			new EntityBoundSoundInstance(
				PkSounds.LANDING_ROLL.get(),
				SoundSource.PLAYERS,
				ROLL_SOUND_VOLUME,
				ROLL_SOUND_PITCH,
				player,
				player.getRandom().nextLong()
			)
		);
	}

	public static void playAnimation(Player player) {
		if (!(player instanceof LocalPlayer localPlayer)) return;
		boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
		if (isFirstPerson) {
			localPlayer.setXRot(0);// 第一人称时重置玩家的俯仰角，使玩家在抬头时翻滚结束也能朝前看，手感更好
		}
		// 播放摄像机翻滚动画
		ResourceLocation animId = ResourceLocation.fromNamespaceAndPath("parkour", "camera_animations/landing_roll.json/landing_roll");
		CameraAnimationController.INSTANCE.play(animId);

		// 播放玩家翻滚动画
		PlayerAnimationManager.playOneOffAnimation(
			localPlayer, PkPlayerAnimations.LANDING_ROLL.id, false
		);
	}
}
