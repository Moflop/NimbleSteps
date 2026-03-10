package mod.arcomit.nimblesteps.v2.core.sensor.sensor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-02-20
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID, value = Dist.CLIENT)
public class SensorDebugRenderer {

	// 假设用一个布尔值控制是否开启 Debug
	public static boolean RENDER_DEBUG_BOXES = true;

	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (!RENDER_DEBUG_BOXES || event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) return;

		MovementStateContext state = MovementStateContext.get(player);
		SensorManager sensorManager = state.getSensorManager(); // 假设你在 Context 里加了这个

		PoseStack poseStack = event.getPoseStack();
		Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();

		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

		poseStack.pushPose();
		// 移动到世界原点，因为 Sensor 保存的是 World AABB
		poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

		for (AbstractBoxSensor sensor : sensorManager.getAllSensors()) {
			if (!sensor.shouldDebugRender(player)) {
				continue;
			}
			AABB box = sensor.getCurrentWorldBox(player);
			boolean isHit = sensor.isColliding(player);

			// 发生碰撞画红色，未发生画绿色
			float r = isHit ? 1.0F : 0.0F;
			float g = isHit ? 0.0F : 1.0F;
			float b = 0.0F;

			LevelRenderer.renderLineBox(
				poseStack, vertexConsumer,
				box.minX, box.minY, box.minZ,
				box.maxX, box.maxY, box.maxZ,
				r, g, b, 1.0F
			);
		}

		poseStack.popPose();
	}

}
