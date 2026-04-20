package mod.arcomit.parkour.v2.content.behavior.landingroll;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.content.init.PkPlayerAnimations;
import mod.arcomit.parkour.v2.content.init.PkSounds;
import mod.arcomit.parkour.v2.core.animation.camera.CameraAnimationController;
import mod.arcomit.parkour.v2.core.animation.player.PlayerAnimation;
import mod.arcomit.parkour.v2.core.animation.player.PlayerAnimationManager;
import mod.arcomit.parkour.v2.core.animation.player.network.RequestPlayActionC2SPayload;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 落地翻滚状态
 *
 * @author Arcomit
 * @since 2026-04-13
 */
public class LandingRollState extends AbstractParkourState {
	public static final int LANDING_ROLL_DURATION = 8;
	private static final float ROLL_SOUND_VOLUME = 1.0f;
	private static final float ROLL_SOUND_PITCH = 1.0f;

	public LandingRollState() {
		registerTransitions(
			IParkourStateTransition.onTick(
				PkParkourStates.DEFAULT::get,
				player -> !this.isValid(player)
			)
		);
	}

	@Override
	public void onEnter(Player player) {
		LandingRollLogic.applyRollEffects(player);

		Level level = player.level();
		if (level.isClientSide) {
			if (player.isLocalPlayer()) {
				boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
				if (isFirstPerson) {
					player.setXRot(0);
				}
				ResourceLocation animId = ResourceLocation.fromNamespaceAndPath("parkour", "camera_animations/landing_roll.json/landing_roll");
				CameraAnimationController.INSTANCE.play(animId);

				PlayerAnimationManager.playOneOffAnimation(
					(AbstractClientPlayer) player, PkPlayerAnimations.LANDING_ROLL.id, false
				);
			}

			Minecraft.getInstance().getSoundManager().play(
				new EntityBoundSoundInstance(
					PkSounds.LANDING_ROLL.get(), SoundSource.PLAYERS,
					ROLL_SOUND_VOLUME, ROLL_SOUND_PITCH, player, player.getRandom().nextLong()
				)
			);
		} else {
			level.playSound(
				player, player.getX(), player.getY(), player.getZ(),
				PkSounds.LANDING_ROLL.get(), SoundSource.PLAYERS,
				ROLL_SOUND_VOLUME, ROLL_SOUND_PITCH
			);
		}
	}

	@Override
	public EntityDimensions getCustomDimensions(Player player) {
		return EntityDimensions.fixed(0.6f, 0.6f).withEyeHeight(0.4f);
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	/**
	 * 校验落地翻滚的基础环境是否合法
	 */
	public static boolean isBaseValid(Player player) {
		return ServerConfig.enableLandingRoll
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	@Override
	public boolean canEnter(Player player) {
		GroundData groundData = ParkourContext.get(player).groundData();
		return isBaseValid(player) && groundData.getLandingRollWindow() > 0;
	}

	@Override
	public boolean isValid(Player player) {
		StateData stateData = ParkourContext.get(player).stateData();
		return isBaseValid(player) && stateData.getTicksInState() < LANDING_ROLL_DURATION;
	}
}