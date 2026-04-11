package mod.arcomit.parkour.v2.content.behavior.slide;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.content.init.PkSounds;
import mod.arcomit.parkour.v2.core.animation.ParkourAnim;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 滑铲状态
 * * @author Arcomit
 */
public class SlideState extends AbstractParkourState {
	public static final int SLIDE_DURATION = 10; // 滑铲默认持续时间（以刻为单位）
	private static final float SLIDE_SOUND_VOLUME = 1.0f;
	private static final float SLIDE_SOUND_PITCH = 1.0f;

	public SlideState() {
		registerTransitions(
			// 当状态不再合法（如时间到、非冲刺等），或者玩家按下了后退键（S键）时，退回默认状态
			IParkourStateTransition.onTick(
				PkParkourStates.DEFAULT::get,
				player -> !this.isValid(player) || player.input.down
			)
		);
	}

	@Override
	public void onEnter(Player player) {
		GroundData groundData = ParkourContext.get(player).groundData();
		groundData.setSlideCooldown(ServerConfig.slideCooldown);
		player.resetFallDistance();

		if (player instanceof LocalPlayer localPlayer) {
			Input playerInput = localPlayer.input;
			float forwardImpulse = playerInput.forwardImpulse;
			float leftImpulse = playerInput.leftImpulse;

			float yRotRad = player.getYRot() * Mth.DEG_TO_RAD;

			float sin = Mth.sin(yRotRad);
			float cos = Mth.cos(yRotRad);

			// 计算相对于视角的运动矢量
			double motionX = leftImpulse * cos - forwardImpulse * sin;
			double motionZ = forwardImpulse * cos + leftImpulse * sin;

			if (!player.onGround()) {
				//todo: 滑铲重置墙跳？（待定）groundData.resetLastWallJumpDirection();
				if (ServerConfig.enableTapStrafing) {
					double targetYRot = Math.toDegrees(Math.atan2(-motionX, motionZ));
					float currentYRot = player.getYRot();
					float diff = Mth.wrapDegrees((float)targetYRot - currentYRot);

					player.setYRot(currentYRot + diff);
					player.yRotO = player.getYRot();
				}
			}

			Vec3 motion = new Vec3(motionX, 0, motionZ).normalize()
				.scale(ServerConfig.slideBoostSpeed);
			player.setDeltaMovement(
				player.getDeltaMovement().add(motion)
			);
		}

		Level level = player.level();
		if (level.isClientSide) {
			Minecraft.getInstance().getSoundManager().play(
				new EntityBoundSoundInstance(
					PkSounds.SLIDE.get(),
					SoundSource.PLAYERS,
					SLIDE_SOUND_VOLUME,
					SLIDE_SOUND_PITCH,
					player,
					player.getRandom().nextLong()));
		} else {
			level.playSound(
				player,
				player.getX(),
				player.getY(),
				player.getZ(),
				PkSounds.SLIDE.get(),
				SoundSource.PLAYERS,
				SLIDE_SOUND_VOLUME,
				SLIDE_SOUND_PITCH);
		}
	}

	/**
	 * 校验滑铲的基础环境与玩家状态是否合法
	 */
	private boolean isBaseValid(Player player) {
		return ServerConfig.enableSlide
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	@Override
	public boolean canEnter(Player player) {
		GroundData groundData = ParkourContext.get(player).groundData();
		return isBaseValid(player)
			&& groundData.getSlideCooldown() <= 0;
	}

	@Override
	public boolean isValid(Player player) {
		StateData stateData = ParkourContext.get(player).stateData();
		return isBaseValid(player)
			&& stateData.getTicksInState() < SLIDE_DURATION;
	}

	@Override
	public EntityDimensions getCustomDimensions(Player player) {
		return EntityDimensions.fixed(0.6f, 0.6f).withEyeHeight(0.4f);
	}

	@Override
	public int generateVariant(Player player) {
		// 滑铲有 2 种随机动画 (变体 0 和 1)
		// 每次进入滑铲前，自动生成一个随机数
		return ThreadLocalRandom.current().nextInt(2);
	}

	@Override
	public ParkourAnim getLinkedAnimation(Player player) {
		int variant = ParkourContext.get(player).stateData().getAnimVariant();
		return switch (variant) {
			case 1 -> ParkourAnim.SLIDE_2;
			default -> ParkourAnim.SLIDE_1;
		};
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}
}