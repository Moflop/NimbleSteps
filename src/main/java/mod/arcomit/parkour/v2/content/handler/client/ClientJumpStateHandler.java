package mod.arcomit.parkour.v2.content.handler.client;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.JumpData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-11
 */
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class ClientJumpStateHandler {
	public static final int JUMP_KEY_RELEASE_GRACE_PERIOD = 6;// 跳跃键松开后仍然判定为按下的宽限期

	//实体跳跃时间
	@SubscribeEvent
	public static void onJump(LivingEvent.LivingJumpEvent event) {
		if (event.getEntity() instanceof LocalPlayer player) {
			JumpData jumpData = ParkourContext.get(player).jumpData();
			jumpData.setTicksSinceLastJump(0);// 设置距离上一次跳跃的Tick
			//TODO:优化改进
			jumpData.setJumped(true);// 设置已起跳过（用于armhang）
		}
	}

	@SubscribeEvent
	public static void onGound(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof LocalPlayer player)) {
			return;
		}
		JumpData jumpData = ParkourContext.get(player).jumpData();
		if (!player.onGround()) {
			return;
		}
		if (jumpData.isJumped()) {
			jumpData.setJumped(false);
		}
	}

	//TODO:优化改进
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		if (event.getEntity() instanceof LocalPlayer player) {
			ParkourContext context = ParkourContext.get(player);
			JumpData jumpData = context.jumpData();
			if (player.input.jumping) {
				// 如果一直按着跳跃键，重置宽限期
				jumpData.setJumpReleaseGraceTicks(JUMP_KEY_RELEASE_GRACE_PERIOD);
			}else {
				int grace = jumpData.getJumpReleaseGraceTicks();
				if (grace > 0) {
					jumpData.setJumpReleaseGraceTicks(grace - 1);
				}
			}
		}
	}
}
