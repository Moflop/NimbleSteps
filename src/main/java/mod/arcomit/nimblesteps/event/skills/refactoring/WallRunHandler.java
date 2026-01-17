package mod.arcomit.nimblesteps.event.skills.refactoring;

import java.util.ArrayList;
import java.util.List;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.init.NsAttachmentTypes;
import mod.arcomit.nimblesteps.init.NsTags;
import mod.arcomit.nimblesteps.network.serverbound.wallrun.ServerboundClampWallRunDurationPacket;
import mod.arcomit.nimblesteps.network.serverbound.wallrun.ServerboundEndWallRunPacket;
import mod.arcomit.nimblesteps.network.serverbound.wallrun.ServerboundStartWallRunPacket;
import mod.arcomit.nimblesteps.utils.CollisionUtils;
import mod.arcomit.nimblesteps.utils.DirectionUtils;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

/**
 * 墙跑处理器。
 *
 * <p>处理墙跑的开始、进行中物理计算以及结束逻辑。
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class WallRunHandler {
	private static final double ZERO_THRESHOLD = 1.0E-7;

	private static final int MAX_WALL_RUN_DURATION_AFTER_JUMP_KEY_RELEASE = 6;
	private static final double VANILLA_SPRINT_SPEED_MULTIPLIER = 2.15;
	private static final double WALL_ADHESION_FACTOR = 0.1;

	private static final float SOUND_DISTANCE_MULTIPLIER = 0.6F;
	private static final float SOUND_VOLUME_MULTIPLIER = 0.15F;

	private static final double HEAD_BOX_MIN_HEIGHT_RATIO = 0.85;
	private static final double FEET_BOX_MAX_HEIGHT_RATIO = 0.3;
	private static final double COLLISION_CHECK_DISTANCE = 0.15;

	private static final int MAX_TICKS_SINCE_JUMP = 15;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void tryStartWallRun(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer player)) {
			return;
		}
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);

		if (!canStartWallRun(player, state)) {
			return;
		}

		startWallRun(state);
		// 立即申请同步位置，防止服务端位置不一致无法开始墙跑
		player.sendPosition();
		PacketDistributor.sendToServer(new ServerboundStartWallRunPacket());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void handleWallRun(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);

		if (!state.isWallRunning()) {
			return;
		}

		// 检查是否满足持续墙跑的条件
		if (!canWallRun(player, state)) {
			endWallRun(state);
			return;
		}

		decrementWallRunDuration(state);
		applyWallRunMovementAndSound(player);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void checkWallRunInterrupt(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer player)) {
			return;
		}

		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (!state.isWallRunning()) {
			return;
		}

		boolean notMovingForward = player.input.forwardImpulse <= ZERO_THRESHOLD;
		if (notMovingForward) {
			endWallRun(state);
			PacketDistributor.sendToServer(new ServerboundEndWallRunPacket());
			return;
		}

		boolean jumpKeyReleased = !player.input.jumping;
		if (jumpKeyReleased) {
			clampWallRunDuration(state);
			PacketDistributor.sendToServer(new ServerboundClampWallRunDurationPacket());
		}
	}

	@SubscribeEvent
	public static void resetWallRunCount(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (player.onGround() && state.getWallRunCount() != 0) {
			state.setWallRunCount(0);
		}
	}

	public static void startWallRun(NimbleStepsState state) {
		state.setWallRunDuration(ServerConfig.wallRunDuration);
		state.setWallRunCount(state.getWallRunCount() + 1);
	}

	public static void endWallRun(NimbleStepsState state) {
		state.setWallRunDuration(0);
	}

	public static void clampWallRunDuration(NimbleStepsState state) {
		state.setWallRunDuration(
			Math.min(state.getWallRunDuration(), MAX_WALL_RUN_DURATION_AFTER_JUMP_KEY_RELEASE));
	}

	private static void decrementWallRunDuration(NimbleStepsState state) {
		int currentDuration = state.getWallRunDuration();
		state.setWallRunDuration(currentDuration - 1);
	}

	/**
	 * 应用墙跑的移动物理效果和音效。
	 */
	private static void applyWallRunMovementAndSound(Player player) {
		Direction facing = player.getDirection();
		Vec3 runDirection = new Vec3(facing.getStepX(), 0, facing.getStepZ()).normalize();

		double attributeSpeed = player.getAttributeValue(Attributes.MOVEMENT_SPEED);
		double targetSpeed = attributeSpeed * VANILLA_SPRINT_SPEED_MULTIPLIER;

		Direction wallDirection = findAvailableWallDirection(player);
		if (wallDirection != null) {
			player.setDeltaMovement(runDirection.scale(targetSpeed));
			// 墙面吸附
			Vec3 wallNormal = new Vec3(wallDirection.getStepX(), 0, wallDirection.getStepZ());
			Vec3 adhesionForce = wallNormal.scale(WALL_ADHESION_FACTOR);
			player.move(MoverType.PLAYER, adhesionForce);
			player.resetFallDistance();
			playWallRunSound(player, wallDirection);
		}
	}

	private static void playWallRunSound(Player player, Direction wallDirection) {
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
		BlockPos playerPos = player.blockPosition();
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
	 * 判断客户端是否满足开始墙跑的条件。
	 */
	private static boolean canStartWallRun(LocalPlayer player, NimbleStepsState state) {
		boolean jumpKeyIsPressed = player.input.jumping;
		boolean isFalling = player.fallDistance > 0f;
		boolean hasForwardImpulse = player.input.forwardImpulse > ZERO_THRESHOLD;
		boolean playerJustJumped = state.getTicksSinceLastJump() <= MAX_TICKS_SINCE_JUMP;
		boolean hasRemainingWallRunCount = state.getWallRunCount() < ServerConfig.maxWallRunCount;
		return ServerConfig.enableWallRun
			&& !state.isWallRunning()
			&& jumpKeyIsPressed
			&& hasForwardImpulse
			&& playerJustJumped
			&& isFalling
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& hasRemainingWallRunCount
			&& canWallRun(player, state);
	}

	/**
	 * 判断双端是否满足持续墙跑的条件。
	 */
	public static boolean canWallRun(Player player, NimbleStepsState state) {
		boolean noColliding = !CollisionUtils.isEntityCollidingWithBlockInDirection(
			player, player.getDirection(), NsTags.Blocks.COMMON_IGNORED_BLOCKS);
		return player.isSprinting()
			&& !player.onGround()
			&& findAvailableWallDirection(player) != null
			&& noColliding
			&& !state.isSliding()
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	/**
	 * 获取玩家可进行墙跑的墙壁方向。
	 * 检查玩家左侧或右侧是否有连续的墙壁碰撞箱（头部和脚部）。
	 *
	 * @param player 玩家实体
	 * @return 可用的墙壁方向，如果没有则返回 null
	 */
	private static Direction findAvailableWallDirection(Player player) {
		List<AABB> checkBoxes = getAabbs(player);
		List<Direction> collisionDirections = new ArrayList<>(2);

		Direction facing = player.getDirection();
		Level level = player.level();

		// 检查右侧
		Direction right = facing.getClockWise();
		if (CollisionUtils.areAllBoxesCollidingWithBlockInDirection(
			level, checkBoxes, right, COLLISION_CHECK_DISTANCE, NsTags.Blocks.COMMON_IGNORED_BLOCKS))
		{
			collisionDirections.add(right);
		}

		// 检查左侧
		Direction left = facing.getCounterClockWise();
		if (CollisionUtils.areAllBoxesCollidingWithBlockInDirection(
			level, checkBoxes, left, COLLISION_CHECK_DISTANCE, NsTags.Blocks.COMMON_IGNORED_BLOCKS))
		{
			collisionDirections.add(left);
		}

		return DirectionUtils.getClosestDirection(player, collisionDirections);
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