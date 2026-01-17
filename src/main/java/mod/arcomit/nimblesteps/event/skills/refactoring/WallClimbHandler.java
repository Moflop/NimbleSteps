package mod.arcomit.nimblesteps.event.skills.refactoring;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.init.NsAttachmentTypes;
import mod.arcomit.nimblesteps.init.NsTags;
import mod.arcomit.nimblesteps.network.serverbound.wallclimb.ServerboundEndWallClimbPacket;
import mod.arcomit.nimblesteps.network.serverbound.wallclimb.ServerboundStartWallClimbPacket;
import mod.arcomit.nimblesteps.utils.CollisionUtils;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 爬墙处理器。
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class WallClimbHandler {
	private static final double ZERO_THRESHOLD = 1.0E-7;

	private static final double WALL_ADHESION_FORCE = 0.1;

	private static final float SOUND_DISTANCE_MULTIPLIER = 1.0F;
	private static final float SOUND_VOLUME_MULTIPLIER = 0.15F;

	private static final double HEAD_BOX_MIN_HEIGHT_RATIO = 0.85;
	private static final double FEET_BOX_MAX_HEIGHT_RATIO = 0.3;
	private static final double COLLISION_CHECK_DISTANCE = 0.1;

	private static final int MAX_TICKS_SINCE_JUMP = 15;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void tryStartWallClimb(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer player)) {
			return;
		}
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);

		if (!canStartWallClimb(player, state)) {
			return;
		}

		startWallClimb(state);
		// 立即申请同步位置，防止服务端位置不一致无法开始爬墙
		player.sendPosition();
		PacketDistributor.sendToServer(new ServerboundStartWallClimbPacket());
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void handleWallClimb(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);

		if (!state.isWallClimbing()) {
			return;
		}

		// 检查是否满足持续墙跑的条件
		if (!canWallClimb(player, state)) {
			endWallClimb(state);
			return;
		}

		decrementWallClimbDuration(state);
		applyWallClimbMovementAndSound(player);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void checkWallClimbInterrupt(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer player)) {
			return;
		}

		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (!state.isWallClimbing()) {
			return;
		}

		boolean notMovingForward = player.input.forwardImpulse <= ZERO_THRESHOLD;
		boolean jumpKeyReleased = !player.input.jumping;
		if (notMovingForward || jumpKeyReleased) {
			endWallClimb(state);
			PacketDistributor.sendToServer(new ServerboundEndWallClimbPacket());
		}
	}

	@SubscribeEvent
	public static void resetWallClimb(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (player.onGround() && state.isHasWallClimbed()) {
			state.setHasWallClimbed(false);
		}
	}

	public static void startWallClimb(NimbleStepsState state) {
		state.setWallClimbDuration(ServerConfig.wallClimbDuration);
		state.setHasWallClimbed(true);
	}

	public static void endWallClimb(NimbleStepsState state) {
		state.setWallClimbDuration(0);
	}

	private static void decrementWallClimbDuration(NimbleStepsState state) {
		int currentDuration = state.getWallClimbDuration();
		state.setWallClimbDuration(currentDuration - 1);
	}

	/**
	 * 应用爬墙的移动物理效果和音效。
	 */
	private static void applyWallClimbMovementAndSound(Player player) {
		player.setDeltaMovement(0, ServerConfig.wallClimbSpeed, 0);
		// 墙面吸附
		Direction facing = player.getDirection();
		Vec3 facingVec = Vec3.atLowerCornerOf(facing.getNormal());
		Vec3 adhesionForce = facingVec.scale(WALL_ADHESION_FORCE);
		player.move(MoverType.PLAYER, adhesionForce);

		player.resetFallDistance();
		playWallClimbSound(player, facing);
	}

	private static void playWallClimbSound(Player player, Direction wallDirection) {
		double deltaX = player.getX() - player.xo;
		double deltaY = player.getY() - player.yo;
		double deltaZ = player.getZ() - player.zo;
		double actualDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

		// 如果实际位移微乎其微，直接返回
		if (actualDistance < ZERO_THRESHOLD) {
			return;
		}

		float distanceTraveled = (float) actualDistance * SOUND_DISTANCE_MULTIPLIER;
		player.moveDist += distanceTraveled;

		// 检查是否达到播放下一步音效的距离阈值
		if (player.moveDist <= player.nextStep) {
			return;
		}
		player.nextStep = player.moveDist + 1.0F;

		// 获取墙壁材质音效
		BlockPos playerPos = BlockPos.containing(player.getX(), player.getY() + player.getBbHeight() * HEAD_BOX_MIN_HEIGHT_RATIO, player.getZ());
		BlockPos wallPos = playerPos.relative(wallDirection);
		Level level = player.level();

		BlockState blockState = level.getBlockState(wallPos);

		if (blockState.isAir()) {
			return;
		}

		SoundType soundType = blockState.getSoundType(level, wallPos, player);
		float volume = soundType.getVolume() * SOUND_VOLUME_MULTIPLIER;
		float pitch = soundType.getPitch();

		level.playSound(
			player,
			wallPos.getX(),
			wallPos.getY(),
			wallPos.getZ(),
			soundType.getStepSound(),
			SoundSource.PLAYERS,
			volume,
			pitch
		);
	}

	/**
	 * 判断客户端是否满足开始爬墙的条件。
	 */
	private static boolean canStartWallClimb(LocalPlayer player, NimbleStepsState state) {
		boolean jumpKeyIsPressed = player.input.jumping;
		boolean isFalling = player.fallDistance > 0f;
		boolean hasForwardImpulse = player.input.forwardImpulse > ZERO_THRESHOLD;
		boolean playerJustJumped = state.getTicksSinceLastJump() <= MAX_TICKS_SINCE_JUMP;
		boolean hasNotClimbedWall = !state.isHasWallClimbed();
		return ServerConfig.enableWallRun
			&& !state.isWallClimbing()
			&& jumpKeyIsPressed
			&& hasForwardImpulse
			&& playerJustJumped
			&& isFalling
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& hasNotClimbedWall
			&& canWallClimb(player, state);
	}

	/**
	 * 判断双端是否满足持续墙跑的条件。
	 */
	public static boolean canWallClimb(Player player, NimbleStepsState state) {
		return player.isSprinting()
			&& !player.onGround()
			&& canClimbOnFacingWall(player, player.getDirection())
			&& !state.isSliding()
			&& !state.isArmHanging()
			&& !state.isWallRunning()
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	private static boolean canClimbOnFacingWall(Player player, Direction direction) {
		// 头部脚部都需要有碰撞
		List<AABB> checkBoxes = getAabbs(player);
		return CollisionUtils.areAllBoxesCollidingWithBlockInDirection(
			player.level(),
			checkBoxes,
			direction,
			COLLISION_CHECK_DISTANCE,
			NsTags.Blocks.COMMON_IGNORED_BLOCKS
		);
	}

	private static @NotNull List<AABB> getAabbs(Player player) {
		Vec3 playerPos = player.position();
		double halfWidth = player.getBbWidth() * 0.5;
		double height = player.getBbHeight();

		// 构造脚部和头部的检测箱
		AABB feetBox = new AABB(
			playerPos.x - halfWidth,
			playerPos.y,
			playerPos.z - halfWidth,
			playerPos.x + halfWidth,
			playerPos.y + height * FEET_BOX_MAX_HEIGHT_RATIO,
			playerPos.z + halfWidth);

		AABB headBox = new AABB(
			playerPos.x - halfWidth,
			playerPos.y + height * HEAD_BOX_MIN_HEIGHT_RATIO,
			playerPos.z - halfWidth,
			playerPos.x + halfWidth,
			playerPos.y + height,
			playerPos.z + halfWidth);

		return List.of(feetBox, headBox);
	}
}
