package mod.arcomit.parkour.v2.content.action.walljump;

import mod.arcomit.parkour.v2.content.action.walljump.network.WallJumpC2SPayload;
import mod.arcomit.parkour.v2.core.context.JumpData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.proxy.ParkourProxies;
import mod.arcomit.parkour.v2.core.statemachine.ParkourStateMachine;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-12
 */
public class WallJumpAction {

	public static void tryJump(Player player) {
		ParkourContext context = ParkourContext.get(player);
		IParkourState currentState = context.stateData().getState();
		if (!WallJumpEligibilityChecke.check(player, currentState)) return;

		// 墙面方向解析
		Direction wallDir = WallJumpDirectionResolver.resolveWallDirection(player, currentState, context);
		if (wallDir == null) return;

		// 跳型解析
		JumpType jumpType = WallJumpTypeResolver.resolve(player, wallDir);
		if (jumpType == JumpType.NONE) return;

		// 同墙防止检查
		JumpData jumpData = context.jumpData();
		int wallDirValue = wallDir.get3DDataValue();
		if (WallJumpSameWallGuard.isSameWallAsLastJump(jumpData, jumpType, wallDirValue)) return;

		// 应用速度
		Vec3 velocity = WallJumpVelocity.computeJumpVelocity(player, jumpType);
		player.setDeltaMovement(velocity);

		// 更新最近跳跃方向（单墙保护）
		if (WallJumpSameWallGuard.isAgainstSingleWall(player)) {
			WallJumpSameWallGuard.recordLastJumpWall(jumpData, jumpType, wallDirValue);
		}

		// 重置状态、播放音效（一次，不再重复）
		jumpData.setTicksSinceLastJump(0);
		ParkourStateMachine.resetToDefaultState(player, context);
		WallJumpSound.playWallJumpSound(player);
		player.resetFallDistance();

		if (player.isLocalPlayer()) {
			ParkourProxies.PLAYER_SERVICES_PROXY.sendPosition(player);
			PacketDistributor.sendToServer(new WallJumpC2SPayload());
		}
	}
}
