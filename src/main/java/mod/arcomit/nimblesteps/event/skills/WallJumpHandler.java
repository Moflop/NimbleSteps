package mod.arcomit.nimblesteps.event.skills;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.init.NsAttachmentTypes;
import mod.arcomit.nimblesteps.init.NsSounds;
import mod.arcomit.nimblesteps.init.NsTags;
import mod.arcomit.nimblesteps.network.serverbound.jump.WallJumpPacket;
import mod.arcomit.nimblesteps.utils.CollisionUtils;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

/**
 * 处理墙跳逻辑的事件处理器。
 *
 * <p>负责监听客户端输入以触发墙跳，并处理墙跳的具体物理逻辑和状态更新。
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class WallJumpHandler {

	private static final double WALL_JUMP_HORIZONTAL_SPEED = 0.7;
	private static final double WALL_JUMP_VERTICAL_SPEED = 0.4;

	// 碰撞检测参数
	private static final double FEET_BOX_MAX_HEIGHT_RATIO = 0.3;
	private static final double HEAD_BOX_MIN_HEIGHT_RATIO = 0.85;
	private static final double COLLISION_CHECK_DISTANCE = 0.15;

	// 音效参数
	private static final float JUMP_SOUND_VOLUME = 1.5f;
	private static final float JUMP_SOUND_PITCH = 1.0f;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onKeyPress(InputEvent.Key event) {
		tryWallJumpOnInput(event.getAction(), event.getKey());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onMousePress(InputEvent.MouseButton.Post event) {
		tryWallJumpOnInput(event.getAction(), event.getButton());
	}

	/**
	 * 统一处理跳跃输入的内部辅助方法。
	 *
	 * @param action 输入动作 (如按下、松开)
	 * @param inputKey 输入的键值或鼠标按钮值
	 */
	@OnlyIn(Dist.CLIENT)
	private static void tryWallJumpOnInput(int action, int inputKey) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;

		if (player == null || mc.screen != null) {
			return;
		}
		if (action != InputConstants.PRESS) {
			return;
		}
		if (inputKey != mc.options.keyJump.getKey().getValue()) {
			return;
		}

		NimbleStepsState state = NimbleStepsState.getNimbleState(player);

		if (canWallJump(player, state)) {
			performWallJump(player, state);
			PacketDistributor.sendToServer(new WallJumpPacket());
		}
	}

	/**
	 * 落地则重置上一次墙跳方向记录。
	 */
	@SubscribeEvent
	public static void resetLastWallJumpDirection(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
		if (player.onGround() && state.getLastWallJumpDirection() != -1) {
			state.resetLastWallJumpDirection();
		}
	}

	/**
	 * 执行墙跳动作。
	 *
	 * <p>更新玩家的数据附件状态（重置墙跑、滑墙等），播放音效并施加物理冲量。
	 *
	 * @param player 执行墙跳的玩家
	 */
	public static void performWallJump(Player player, NimbleStepsState state) {
		updateWallJumpDirectionState(player, state);
		resetMovementStates(state);

		// 施加运动矢量
		Vec3 look = player.getLookAngle();
		Vec3 jumpDir = new Vec3(look.x, 0, look.z).normalize();

		player.level().playSound(
				player,
				player.getX(),
				player.getY(),
				player.getZ(),
				NsSounds.JUMP.get(),
				SoundSource.PLAYERS,
				JUMP_SOUND_VOLUME,
				JUMP_SOUND_PITCH);

		player.setDeltaMovement(
			jumpDir.scale(WALL_JUMP_HORIZONTAL_SPEED).add(0, WALL_JUMP_VERTICAL_SPEED, 0));
		player.resetFallDistance();
	}

	/**
	 * 更新玩家的最后墙跳方向状态。
	 */
	private static void updateWallJumpDirectionState(Player player, NimbleStepsState state) {
		if (state.isArmHanging()) {
			state.setLastWallJumpDirection(state.getArmHangingDirection());
			state.setArmHanging(false);
			state.resetArmHangingDirection();
			return;
		}

		List<Direction> walls = getAvailableJumpableWalls(player);
		Direction facing = player.getDirection();
		boolean isWallMoving = state.isWallRunning() || state.isWallSliding();

		for (Direction wall : walls) {
			boolean valid = false;
			if (isWallMoving) {
				if (wall.get3DDataValue() != state.getLastWallJumpDirection()) {
					valid = true;
				}
			} else {
				if (wall != facing && wall.get3DDataValue() != state.getLastWallJumpDirection()) {
					valid = true;
				}
			}

			if (valid) {
				state.setLastWallJumpDirection(wall.get3DDataValue());
				break;
			}
		}
	}

	/**
	 * 重置与墙跳相关的计时器和状态标记。
	 */
	private static void resetMovementStates(NimbleStepsState state) {
		if (ServerConfig.wallJumpResetWallRun) {
			state.setWallRunDuration(0);
			state.setWallRunCount(0);
		}
		if (ServerConfig.wallJumpResetWallClimb) {
			state.setHasWallClimbed(false);
		}
		state.setWallSliding(false);
		state.setWallSlideJumpReleaseGraceTicks(0);
		state.setTicksSinceLastJump(0);
	}

	/**
	 * 检查玩家当前是否可以执行墙跳。
	 *
	 * @param player 玩家实体
	 * @return 如果满足墙跳条件返回 true
	 */
	public static boolean canWallJump(Player player, NimbleStepsState state) {
		// 1. 基础状态检查（配置禁用、液体中、无法行动等）
		if (isWallJumpDisabledOrRestricted(player)) {
			return false;
		}

		// 2. 场景一：抓墙状态下的跳跃
		if (state.isArmHanging()) {
			return canJumpFromArmHanging(player, state);
		}

		// 获取周围可用的墙面
		List<Direction> availableWalls = getAvailableJumpableWalls(player);
		if (availableWalls.isEmpty()) {
			return false;
		}

		// 3. 场景二：墙跑或滑墙状态下的连招跳跃
		if (state.isWallRunning() || state.isWallSliding()) {
			return canJumpDuringWallAction(availableWalls, state);
		}

		// 4. 场景三：普通腾空状态下的墙跳
		if (!player.onGround()) {
			return canJumpFromAir(player, availableWalls, state);
		}

		return false;
	}

	/** 检查是否因环境或配置原因无法墙跳。 */
	private static boolean isWallJumpDisabledOrRestricted(Player player) {
		return !ServerConfig.enableWallJump
			|| player.isInWater()
			|| player.isInLava()
			|| player.isSwimming()
			|| !PlayerStateUtils.isAbleToAction(player);
	}

	/** 检查抓墙状态下是否可以跳跃（不能向抓附的墙面跳）。 */
	private static boolean canJumpFromArmHanging(Player player, NimbleStepsState state) {
		Direction armHangingDirection = Direction.from3DDataValue(state.getArmHangingDirection());
		// 只有当玩家面朝方向不等于抓墙方向时才允许墙跳（即背对或侧对墙跳）
		return player.getDirection() != armHangingDirection;
	}

	/** 检查墙跑/滑墙期间是否可以跳跃。 */
	private static boolean canJumpDuringWallAction(List<Direction> walls, NimbleStepsState state) {
		int lastJumpDir = state.getLastWallJumpDirection();
		// 只要有一面墙不是上一次跳的那面墙，就可以跳
		return walls.stream()
			.anyMatch(wall -> wall.get3DDataValue() != lastJumpDir);
	}

	/** 检查空中是否可以普通墙跳（不能面朝墙跳，且不能是上一次借力的墙）。 */
	private static boolean canJumpFromAir(Player player, List<Direction> walls, NimbleStepsState state) {
		Direction facing = player.getDirection();
		int lastJumpDir = state.getLastWallJumpDirection();

		return walls.stream()
			.anyMatch(wall ->
				wall != facing && // 不能跳向正对面的墙（那通常是攀爬逻辑）
					wall.get3DDataValue() != lastJumpDir // 不能连续蹬同一面墙
			);
	}

	/**
	 * 获取玩家当前可以进行墙跳的墙壁方向列表。
	 *
	 * <p>通过检测玩家身体底部和头部区域的碰撞箱是否接触到符合条件的方块来判断。
	 *
	 * @param player 目标玩家
	 * @return 可用的墙壁方向列表
	 */
	private static List<Direction> getAvailableJumpableWalls(Player player) {
		List<AABB> checkBoxes = getPlayerAabbs(player);
		ArrayList<Direction> collisionDirections = new ArrayList<>(4);

		for (Direction dir : Direction.Plane.HORIZONTAL) {
			// 必须所有检测箱都在该方向检测到碰撞才算有效墙面
			if (CollisionUtils.areAllBoxesCollidingWithBlockInDirection(
				player.level(), checkBoxes, dir, COLLISION_CHECK_DISTANCE, NsTags.Blocks.SCAFFOLDING_BLOCKS)) {
				collisionDirections.add(dir);
			}
		}

		return collisionDirections;
	}

	private static @NotNull List<AABB> getPlayerAabbs(Player player) {
		Vec3 playerPos = player.position();
		double halfWidth = player.getBbWidth() * 0.5;
		double height = player.getBbHeight();

		// 构建检测箱：脚部和头部
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