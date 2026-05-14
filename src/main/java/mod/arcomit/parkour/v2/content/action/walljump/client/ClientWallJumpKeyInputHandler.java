package mod.arcomit.parkour.v2.content.action.walljump.client;

import com.mojang.blaze3d.platform.InputConstants;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.action.walljump.WallJumpAction;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;


/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-10
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class ClientWallJumpKeyInputHandler {

	// 上一次有效跳跃按下的时间戳（毫秒）
	private static long lastJumpPressMs = 0;
	// 冷却时间（毫秒）
	private static final long JUMP_COOLDOWN_MS = 150;

	// 键盘鼠标按下事件
	@SubscribeEvent
	public static void onKeyAction(InputEvent.Key event) {
		handleKeyAction(event.getAction(), event.getKey());
	}
	@SubscribeEvent
	public static void onMouseAction(InputEvent.MouseButton.Post event) {
		handleKeyAction(event.getAction(), event.getButton());
	}

	private static void handleKeyAction(int action, int inputKey) {
		// 基本条件：玩家存在，没有界面打开，且按下的是跳跃键
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null || mc.screen != null) {
			return;
		}
		if (!isJumpPress(mc, action, inputKey)) {
			return;
		}

		// 冷却检查
		long now = System.currentTimeMillis();
		if (now - lastJumpPressMs < JUMP_COOLDOWN_MS) {
			return;
		}
		lastJumpPressMs = now;

		WallJumpAction.tryJump(player);
	}

	private static boolean isJumpPress(Minecraft mc, int action, int inputKey) {
		if (action != InputConstants.PRESS) {
			return false;
		}
		if (inputKey != mc.options.keyJump.getKey().getValue()) {
			return false;
		}

		return true;
	}
}
