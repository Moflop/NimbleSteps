package mod.arcomit.parkour.v2.content.behavior.landingroll;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.content.init.PkSounds;
import mod.arcomit.parkour.v2.core.animation.camera.CameraAnimationController;
import mod.arcomit.parkour.v2.core.animation.player.PlayerAnimmation;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 落地翻滚状态 (重构版)
 * @author Arcomit
 */
public class LandingRollState extends AbstractParkourState {
	public static final int LANDING_ROLL_DURATION = 8; // 落地翻滚持续时间（以刻为单位）
	private static final float ROLL_SOUND_VOLUME = 1.0f;
	private static final float ROLL_SOUND_PITCH = 1.0f;

	public LandingRollState() {
		registerTransitions(
			// 当状态不再合法（如时间到、或者玩家进入水中等），退回默认状态
			IParkourStateTransition.onTick(
				PkParkourStates.DEFAULT::get,
				player -> !this.isValid(player)
			)
		);
	}

	@Override
	public void onEnter(Player player) {
		// 进入状态时重置部分数据
		GroundData groundData = ParkourContext.get(player).groundData();
		groundData.setLandingRollWindow(0); // 消耗掉翻滚窗口

		Level level = player.level();
		if (level.isClientSide) {
			if (player.isLocalPlayer()) {
				boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
				if (isFirstPerson) {
					player.setXRot(0);
				}
				ResourceLocation animId = ResourceLocation.fromNamespaceAndPath("parkour", "camera_animations/landing_roll.json/landing_roll");
				CameraAnimationController.INSTANCE.play(animId);
			}

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
		} else {
			level.playSound(
				player,
				player.getX(), player.getY(), player.getZ(),
				PkSounds.LANDING_ROLL.get(),
				SoundSource.PLAYERS,
				ROLL_SOUND_VOLUME,
				ROLL_SOUND_PITCH
			);
			// 给予速度增益
			player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0));
		}
	}

	/**
	 * 校验翻滚的基础环境与玩家状态是否合法
	 */
	private boolean isBaseValid(Player player) {
		return ServerConfig.enableLandingRoll
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	@Override
	public boolean canEnter(Player player) {
		GroundData groundData = ParkourContext.get(player).groundData();
		// 判断是否允许进入翻滚状态（必须在有效窗口期内）
		return isBaseValid(player) && groundData.getLandingRollWindow() > 0;
	}

	@Override
	public boolean isValid(Player player) {
		StateData stateData = ParkourContext.get(player).stateData();
		// 利用系统自动记录的 ticksInState 判断状态是否该结束
		return isBaseValid(player) && stateData.getTicksInState() < LANDING_ROLL_DURATION;
	}

	@Override
	public EntityDimensions getCustomDimensions(Player player) {
		return EntityDimensions.fixed(0.6f, 0.6f).withEyeHeight(0.4f);
	}

	@Override
	public PlayerAnimmation getLinkedAnimation(Player player) {
		return PlayerAnimmation.LANDING_ROLL;
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}
}