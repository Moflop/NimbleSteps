//package mod.arcomit.nimblesteps.event.skills;
//
//import mod.arcomit.nimblesteps.NimbleStepsMod;
//import mod.arcomit.nimblesteps.ServerConfig;
//import mod.arcomit.nimblesteps.v2.context.MovementStateContext;
//import mod.arcomit.nimblesteps.network.serverbound.jump.ServerboundSupportWallJumpPacket;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.core.Direction;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.phys.Vec3;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.api.distmarker.OnlyIn;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.event.tick.PlayerTickEvent;
//import net.neoforged.neoforge.network.PacketDistributor;
//
///**
// * 撑墙跳处理器。
// *
// * @author Arcomit
// * @since 2026-01-04
// */
//@EventBusSubscriber(modid = NimbleStepsMod.MODID)
//public class SupportWallJumpHandler {
//
//	private static final double SUPPORT_WALL_JUMP_HORIZONTAL_SPEED = 0.2; // 撑墙跳水平速度
//	private static final double SUPPORT_WALL_JUMP_VERTICAL_SPEED = 0.6; // 撑墙跳垂直速度
//
//	@OnlyIn(Dist.CLIENT)
//	@SubscribeEvent
//	public static void trySupportWallJump(PlayerTickEvent.Post event) {
//		if (!(event.getEntity() instanceof LocalPlayer localPlayer)) {
//			return;
//		}
//		MovementStateContext state = MovementStateContext.getNimbleState(localPlayer);
//
//		boolean jumpNoPressed = !localPlayer.input.jumping;
//		if (jumpNoPressed) {
//			return;
//		}
//
//		if (!canSupportWallJump(localPlayer, state)) {
//			return;
//		}
//
//		useSupportWallJump(localPlayer, state);
//		PacketDistributor.sendToServer(new ServerboundSupportWallJumpPacket());
//	}
//
//	public static void useSupportWallJump(Player player, MovementStateContext state) {
//		ArmhangHandler.endArmhang(state);
//		// 如果配置允许，重置爬墙状态
//		if (ServerConfig.supportWallJumpResetWallClimb) {
//			state.setHasWallClimbed(false);
//		}
//
//		Vec3 look = player.getLookAngle();
//		Vec3 jumpDir = new Vec3(look.x, 0, look.z).normalize();
//		player.setDeltaMovement(
//			jumpDir.scale(SUPPORT_WALL_JUMP_HORIZONTAL_SPEED).add(0, SUPPORT_WALL_JUMP_VERTICAL_SPEED, 0));
//		// 重置距离上移除跳跃的时间
//		state.setTicksSinceLastJump(0);
//
//		player.resetFallDistance();
//	}
//
//	private static boolean isFacingArmHangingDirection(Player player, MovementStateContext state) {
//		Direction clingDir = Direction.from3DDataValue(state.getArmHangingDirection());
//		Direction facing = player.getDirection();
//		return clingDir == facing;
//	}
//
//	public static boolean canSupportWallJump(Player player, MovementStateContext state) {
//		return ServerConfig.enableSupportWallJump
//			&& state.isArmHanging()
//			&& isFacingArmHangingDirection(player, state);
//	}
//
//}
