package mod.arcomit.parkour.v2.core.animation.camera;

import mod.arcomit.parkour.ParkourMod;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

/**
 * 处理摄像机动画对玩家视角的渲染影响。
 */
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class CameraAnimationEventHandler {

	@SubscribeEvent
	public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
		CameraAnimationController controller = CameraAnimationController.INSTANCE;

		// 依然要在每帧推演时间，保证即使切到第三人称，动画时间依然正常流逝
		controller.tick();

		// 检查是否为第一人称
		boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

		// 仅在播放动画 且 处于第一人称时，才应用旋转
		if (controller.isPlaying() && isFirstPerson) {
			float[] rotation = controller.getCurrentRotation();

			event.setPitch(event.getPitch() + rotation[0]);
			event.setYaw(event.getYaw() + rotation[1]);
			event.setRoll(event.getRoll() - rotation[2]);
		}
	}
}