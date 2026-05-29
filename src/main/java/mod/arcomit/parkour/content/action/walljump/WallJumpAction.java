package mod.arcomit.parkour.content.action.walljump;

import mod.arcomit.parkour.core.context.JumpData;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.statemachine.ParkourStateMachine;
import mod.arcomit.parkour.core.statemachine.state.IParkourState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class WallJumpAction {

	public static void execute(Player player) {
		ParkourContext context = ParkourContext.get(player);
		IParkourState currentState = context.stateData().getState();
		if (!WallJumpEligibilityChecker.check(player, currentState)) return;

		// 墙面方向解析
		Direction wallDir = WallJumpDirectionResolver.resolveWallDirection(player, currentState, context);
		if (wallDir == null) return;

		// 跳型解析
		WallJumpType wallJumpType = WallJumpTypeResolver.resolve(player, wallDir);
		if (wallJumpType == WallJumpType.NONE) return;

		// 同墙防止检查
		JumpData jumpData = context.jumpData();
		if (WallJumpSameWallGuard.isSameWallAsLastJump(jumpData, wallJumpType, wallDir)) return;

		// 应用速度
		Vec3 velocity = WallJumpVelocity.computeJumpVelocity(player, wallJumpType);
		player.setDeltaMovement(velocity);

		// 更新最近跳跃方向（只挨着一面墙时记录，如果挨着多面前可以无限次跳跃所以无需记录。）
		if (WallJumpSameWallGuard.isAgainstSingleWall(player)) {
			WallJumpSameWallGuard.recordLastJumpWall(jumpData, wallJumpType, wallDir);
		}

		// 重置状态、播放音效
		jumpData.setTicksSinceLastJump(0);
		ParkourStateMachine.resetToDefaultState(player, context);
		WallJumpSound.playWallJumpSound(player);
		player.resetFallDistance();
	}
}
