package mod.arcomit.nimblesteps.event.skills;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.init.NsAttachmentTypes;
import mod.arcomit.nimblesteps.init.NsTags;
import mod.arcomit.nimblesteps.network.serverbound.wallslide.ServerboundUpdateWallSlideStatePacket;
import mod.arcomit.nimblesteps.utils.CollisionUtils;
import mod.arcomit.nimblesteps.utils.DirectionUtils;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 滑墙处理器。
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class WallSlideHandler {
	private static final int WALL_SLIDE_GRACE_PERIOD = 6; // 松开跳跃键后最大滑墙持续时间
	private static final double WALL_ADHESION_FORCE = 0.1; // 墙面吸附力
	private static final double HORIZONTAL_SLOWDOWN = 0.8; // 水平方向减速比例
	private static final double VERTICAL_SLOWDOWN = 0.7; // 垂直方向减速比例

	private static final double HEAD_BOX_MIN_HEIGHT_RATIO = 0.85; // 头部检测箱最小高度比例
	private static final double FEET_BOX_MAX_HEIGHT_RATIO = 0.3; // 脚部检测箱最大高度比例
	private static final double COLLISION_CHECK_DISTANCE = 0.15; // 墙面碰撞检测距离

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void tryStartWallSlide(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer player)) {
			return;
		}
		NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
		if (!canWallSlide(player, state)) {
			return;
		}
		if (state.isWallSliding()) {
			return;
		}
		boolean jumpIsPressed = player.input.jumping;
		if (jumpIsPressed) {
			state.setWallSliding(true);
			// 立即申请同步位置，防止服务端位置不一致无法开始滑墙
			player.sendPosition();
			PacketDistributor.sendToServer(new ServerboundUpdateWallSlideStatePacket(true));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void handlerWallSlideMovement(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
		if (state.isWallSliding()) {
			useWallSlideMovement(player);
		}
	}

	@SubscribeEvent
	public static void checkWallSlideValidity(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
		if (!state.isWallSliding()) {
			return;
		}
		if (!canWallSlide(player, state)) {
			endWallSliding(state);
			return;
		}

		if (player instanceof LocalPlayer localPlayer) {
			boolean releaseJump = !localPlayer.input.jumping;
			if (releaseJump) {
				updateWallSlideGracePeriod(state);
			}
		}
	}

	public static void startWallSliding(NimbleStepsState state) {
		state.setWallSliding(true);
	}

	public static void endWallSliding(NimbleStepsState state) {
		state.setWallSliding(false);
		state.setWallSlideJumpReleaseGraceTicks(0);
	}

	private static void useWallSlideMovement(Player player) {
		Vec3 motion = player.getDeltaMovement();

		Direction wallDir = findAvailableWallDirection(player);
		if (wallDir != null) {
			Vec3 slowedMotion = motion.multiply(HORIZONTAL_SLOWDOWN, VERTICAL_SLOWDOWN, HORIZONTAL_SLOWDOWN);
			player.setDeltaMovement(slowedMotion);
			// 墙面吸附
			Vec3 wallDirVec = new Vec3(wallDir.getStepX(), 0, wallDir.getStepZ());
			Vec3 adhesionForce = wallDirVec.scale(WALL_ADHESION_FORCE);
			player.move(MoverType.PLAYER, adhesionForce);
			player.resetFallDistance();
		}
	}

	private static void updateWallSlideGracePeriod(NimbleStepsState state) {
		int ticks = state.getWallSlideJumpReleaseGraceTicks();
		if (ticks == 0) {
			ticks = WALL_SLIDE_GRACE_PERIOD;
		} else {
			ticks--;
		}
		state.setWallSlideJumpReleaseGraceTicks(ticks);

		if (ticks <= 0) {
			endWallSliding(state);
			PacketDistributor.sendToServer(new ServerboundUpdateWallSlideStatePacket(false));
		}
	}

	public static boolean canWallSlide(Player player, NimbleStepsState state) {
		boolean isFallingOnAir = player.getDeltaMovement().y() < 0 && !player.onGround();
		return ServerConfig.enableWallSlide
			&& isFallingOnAir
			&& findAvailableWallDirection(player) != null
			&& !state.isSliding()
			&& !state.isWallRunning()
			&& !state.isWallClimbing()
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	private static Direction findAvailableWallDirection(Player player) {
		List<AABB> checkBoxes = getAabbs(player);
		ArrayList<Direction> collisionDirections = new ArrayList<>(4);

		for (Direction dir : Direction.Plane.HORIZONTAL) {
			if (CollisionUtils.areAllBoxesCollidingWithBlockInDirection(
				player.level(), checkBoxes, dir, COLLISION_CHECK_DISTANCE, NsTags.Blocks.COMMON_IGNORED_BLOCKS))
			{
				collisionDirections.add(dir);
			}
		}

		return DirectionUtils.getClosestDirection(player, collisionDirections);
	}

	private static @NotNull List<AABB> getAabbs(Player player) {
		Vec3 playerPos = player.position();
		double halfWidth = player.getBbWidth() / 2;
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
