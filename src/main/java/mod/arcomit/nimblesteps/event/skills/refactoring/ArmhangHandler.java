package mod.arcomit.nimblesteps.event.skills.refactoring;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.init.NsTags;
import mod.arcomit.nimblesteps.utils.CollisionUtils;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-01-16
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class ArmhangHandler {
	private static final double COLLISION_CHECK_DISTANCE = 0.5;
	private static final double BOTTOM_COLLISION_HEIGHT = 0.62;

	private static final double HANGING_POINT_ADHESION_FACTOR = 0.1;

	@SubscribeEvent
	public static void tryStartArmhang(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (!canStartArmhang(player, state)) {
			return;
		}
		Direction facing = player.getDirection();
		if (isClimbableAtDirectionAndPosition(player, player.position(), facing)) {
			startArmhang(state, facing);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void handlerArmhang(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		NimbleStepsState state = NimbleStepsState.getNimbleState(player);
		if (!state.isArmHanging()) {
			return;
		}
		if (canMaintainArmhang(player, state)) {
			//applyArmhangMovement(player, state);
		}
	}

	@SubscribeEvent
	public static void checkArmhangInterrupt(PlayerTickEvent.Post event) {

	}

	private static void startArmhang(NimbleStepsState state, Direction direction) {
		state.setArmHanging(true);
		state.setArmHangingDirection(direction.get3DDataValue());
	}

	private static void endArmhang(NimbleStepsState state) {
		state.setArmHanging(false);
		state.setArmHangingDirection(0);
	}

	private static void applyArmhangMovement(Player player, NimbleStepsState state) {
		player.setPos(player.getX(), player.getY(), player.getZ());
		player.setDeltaMovement(Vec3.ZERO);
		// 抓握点吸附
		Direction armhangingDirection = Direction.from3DDataValue(state.getArmHangingDirection());
		Vec3 armhangingNormal = new Vec3(armhangingDirection.getStepX(), 0, armhangingDirection.getStepZ());
		Vec3 adhesionForce = armhangingNormal.scale(HANGING_POINT_ADHESION_FACTOR);
		player.move(MoverType.PLAYER, adhesionForce);


		player.resetFallDistance();
	}

	private static boolean canStartArmhang(Player player, NimbleStepsState state) {
		boolean isFalling = player.fallDistance > 0f;
		return ServerConfig.enableWallCling
			&& !state.isArmHanging()
			&& !player.onGround()
			&& state.isHasJumped()
			&& isFalling
			&& !player.isShiftKeyDown()
			&& canArmhang(player, state);
	}

	private static boolean canMaintainArmhang(Player player, NimbleStepsState state) {
		return canArmhang(player, state);
	}

	private static boolean canArmhang(Player player, NimbleStepsState state) {
		return !state.isCrawling()
			&& !state.isSliding()
			&& !player.onClimbable()
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	private static boolean isClimbableAtDirectionAndPosition(Player player, Vec3 position, Direction direction) {
		return isClimbableAtSpecificHeight(player, position, direction, player.getEyeHeight())
			|| isClimbableAtSpecificHeight(player, position, direction, player.getBbHeight());
	}

	private static boolean isClimbableAtSpecificHeight(
		Player player, Vec3 position, Direction direction, double baseHeight) {
		double halfWidth = player.getBbWidth() * 0.5;
		double topOffset = player.getBbHeight() - player.getEyeHeight();

		double minX = position.x - halfWidth;
		double maxX = position.x + halfWidth;
		double minZ = position.z - halfWidth;
		double maxZ = position.z + halfWidth;

		double baseY = position.y + baseHeight;

		// 顶部检测：必须有抓握点，没有方块阻挡
		AABB topBox = new AABB(minX, baseY, minZ, maxX, baseY + topOffset, maxZ);
		// 底部检测：必须有方块支撑点
		AABB bottomBox = new AABB(minX, baseY, minZ, maxX, baseY - BOTTOM_COLLISION_HEIGHT, maxZ);

		return CollisionUtils.isCollidingWithBlockInDirection(
			player.level(),
			bottomBox,
			direction,
			COLLISION_CHECK_DISTANCE,
			NsTags.Blocks.SCAFFOLDING_BLOCKS)
			&& !CollisionUtils.isCollidingWithBlockInDirection(
			player.level(),
			topBox,
			direction,
			COLLISION_CHECK_DISTANCE,
			NsTags.Blocks.SCAFFOLDING_BLOCKS);
	}
}
