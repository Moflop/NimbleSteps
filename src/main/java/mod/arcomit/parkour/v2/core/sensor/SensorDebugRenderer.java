package mod.arcomit.parkour.v2.core.sensor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.context.MovementStateContext;
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
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-02-20
 */
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class SensorDebugRenderer {

	// 渲染总开关
	public static boolean RENDER_DEBUG_BOXES = !FMLLoader.isProduction();;

	// 分类渲染开关
	public static boolean RENDER_WALL = false;
	public static boolean RENDER_JUMP = false;
	public static boolean RENDER_ARMHANG = false;

	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (!RENDER_DEBUG_BOXES || event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) return;

		MovementStateContext state = MovementStateContext.get(player);
		SensorManager sensorManager = state.getSensorManager();

		PoseStack poseStack = event.getPoseStack();
		Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();

		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

		poseStack.pushPose();
		// 移动到世界原点，因为 Sensor 保存的是 World AABB
		poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

		// 获取玩家无插值的逻辑位置
		Vec3 logicPos = player.position();
		// 获取玩家带有 Partial Tick 插值的平滑渲染位置
		float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false); // NeoForge 1.20+ 获取方式
		double smoothX = net.minecraft.util.Mth.lerp(partialTick, player.xo, player.getX());
		double smoothY = net.minecraft.util.Mth.lerp(partialTick, player.yo, player.getY());
		double smoothZ = net.minecraft.util.Mth.lerp(partialTick, player.zo, player.getZ());

		// 计算渲染需要补偿的偏移量
		double offsetX = smoothX - logicPos.x;
		double offsetY = smoothY - logicPos.y;
		double offsetZ = smoothZ - logicPos.z;

		for (AbstractBoxSensor sensor : sensorManager.getAllSensors()) {
			if (!sensor.shouldDebugRender(player)) continue;

			AABB box = sensor.getCurrentWorldBox(player);
			boolean isHit = sensor.isColliding(player);

			// 给渲染的框加上偏移量使其丝滑，但不影响传感器内部缓存的逻辑 box
			AABB smoothBox = box.move(offsetX, offsetY, offsetZ);

			float r = isHit ? 1.0F : 0.0F;
			float g = isHit ? 0.0F : 1.0F;
			float b = 0.0F;

			LevelRenderer.renderLineBox(
				poseStack, vertexConsumer,
				smoothBox.minX, smoothBox.minY, smoothBox.minZ,
				smoothBox.maxX, smoothBox.maxY, smoothBox.maxZ,
				r, g, b, 1.0F
			);
		}

		poseStack.popPose();
	}

}
