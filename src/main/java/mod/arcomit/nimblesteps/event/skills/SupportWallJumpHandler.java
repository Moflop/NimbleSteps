package mod.arcomit.nimblesteps.event.skills;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.init.NsAttachmentTypes;
import mod.arcomit.nimblesteps.network.serverbound.jump.SupportWallJumpPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 撑墙跳处理器。
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class SupportWallJumpHandler {

	private static final double SUPPORT_WALL_JUMP_HORIZONTAL_SPEED = 0.2;
	private static final double SUPPORT_WALL_JUMP_VERTICAL_SPEED = 0.6;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void handleShallowSwimming(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if (!(player instanceof LocalPlayer localPlayer)) return;
		if (!localPlayer.input.jumping) return;
		if (!canSupportWallJump(localPlayer)) return;
		useSupportWallJump(player);
		PacketDistributor.sendToServer(new SupportWallJumpPacket());
	}

	public static void useSupportWallJump(Player player) {
		NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
		state.setArmHanging(false);
		state.setArmHangingDirection(-1);
		if (ServerConfig.supportWallJumpResetWallClimb) {
			state.setHasWallClimbed(false);
		}
		// 重置距离上移除跳跃的时间
		state.setTicksSinceLastJump(0);

		Vec3 look = player.getLookAngle();
		Vec3 jumpDir = new Vec3(look.x, 0, look.z).normalize();


		player.setDeltaMovement(jumpDir.scale(SUPPORT_WALL_JUMP_HORIZONTAL_SPEED).add(0, SUPPORT_WALL_JUMP_VERTICAL_SPEED, 0));
		player.resetFallDistance();
	}

	public static boolean canSupportWallJump(Player player) {
		NimbleStepsState state = player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
		if (!state.isArmHanging() || !ServerConfig.enableSupportWallJump) return false;

		Direction clingDir = Direction.from3DDataValue(state.getArmHangingDirection());
		Direction facing = player.getDirection();
		return clingDir == facing;
	}

}
