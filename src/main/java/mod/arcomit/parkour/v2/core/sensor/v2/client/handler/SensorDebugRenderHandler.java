package mod.arcomit.parkour.v2.core.sensor.v2.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.sensor.v2.SensorManager;
import mod.arcomit.parkour.v2.core.sensor.v2.ISensor;
import mod.arcomit.parkour.v2.core.sensor.v2.SensorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-14
 */
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class SensorDebugRenderHandler {
	public static DebugType DEBUG_TYPE = DebugType.NONE;

	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) return;

		PoseStack poseStack = event.getPoseStack();
		Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();

		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

		poseStack.pushPose();
		poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

		Vec3 logicPos = player.position();
		float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
		double smoothX = Mth.lerp(partialTick, player.xo, player.getX());
		double smoothY = Mth.lerp(partialTick, player.yo, player.getY());
		double smoothZ = Mth.lerp(partialTick, player.zo, player.getZ());

		double offsetX = smoothX - logicPos.x;
		double offsetY = smoothY - logicPos.y;
		double offsetZ = smoothZ - logicPos.z;

		for (SensorType sensorType : SensorManager.SENSOR_REGISTRY.keySet()) {
			ISensor sensor = SensorManager.SENSOR_REGISTRY.get(sensorType);
			if (!sensor.shouldDebugRender(player)) continue;

			SensorManager.CollisionSnapshot collisionSnapshot = SensorManager.getCollisionSnapshot(player, sensorType);
			List<AABB> boxs = collisionSnapshot.cacheBoxs();
			boolean isColliding = collisionSnapshot.collided();

			float r = isColliding ? 1.0F : 0.0F;
			float g = isColliding ? 0.0F : 1.0F;
			float b = 0.0F;
			for (AABB box : boxs) {
				AABB smoothBox = box.move(offsetX, offsetY, offsetZ);
				LevelRenderer.renderLineBox(
					poseStack, vertexConsumer,
					smoothBox.minX, smoothBox.minY, smoothBox.minZ,
					smoothBox.maxX, smoothBox.maxY, smoothBox.maxZ,
					r, g, b, 1.0F
				);
			}
		}

		poseStack.popPose();
	}
}
