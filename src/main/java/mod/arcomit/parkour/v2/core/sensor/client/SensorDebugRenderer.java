//package mod.arcomit.parkour.v2.core.sensor.client;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import mod.arcomit.parkour.ParkourMod;
//import mod.arcomit.parkour.v2.core.sensor.SensorManager;
//import mod.arcomit.parkour.v2.core.sensor.SensorResult;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.LevelRenderer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.util.Mth;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.phys.AABB;
//import net.minecraft.world.phys.Vec3;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.api.distmarker.OnlyIn;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.fml.loading.FMLLoader;
//import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
//
///**
// * Sensor 调试渲染器。
// *
// * @author Arcomit
// */
//@OnlyIn(Dist.CLIENT)
//@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
//public class SensorDebugRenderer {
//
//	public static boolean RENDER_DEBUG_BOXES = !FMLLoader.isProduction();
//	public static boolean RENDER_WALL = false;
//	public static boolean RENDER_JUMP = false;
//	public static boolean RENDER_ARMHANG = false;
//
//	@SubscribeEvent
//	public static void onRenderLevelStage(RenderLevelStageEvent event) {
//		if (!RENDER_DEBUG_BOXES || event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
//
//		Minecraft mc = Minecraft.getInstance();
//		Player player = mc.player;
//		if (player == null) return;
//
//		Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();
//		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
//		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
//
//		PoseStack poseStack = event.getPoseStack();
//		poseStack.pushPose();
//		poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
//
//		Vec3 renderOffset = calculateRenderOffset(player, event.getPartialTick().getGameTimeDeltaPartialTick(false));
//
//		for (SensorManager.SensorDef def : SensorManager.getAll()) {
//			if (!def.shouldRender().test(player)) continue;
//			SensorResult result = def.check().apply(player);
//			renderResultBox(poseStack, vertexConsumer, result, renderOffset);
//		}
//
//		poseStack.popPose();
//	}
//
//	private static Vec3 calculateRenderOffset(Player player, float partialTick) {
//		double smoothX = Mth.lerp(partialTick, player.xo, player.getX());
//		double smoothY = Mth.lerp(partialTick, player.yo, player.getY());
//		double smoothZ = Mth.lerp(partialTick, player.zo, player.getZ());
//		return new Vec3(smoothX - player.getX(), smoothY - player.getY(), smoothZ - player.getZ());
//	}
//
//	private static void renderResultBox(PoseStack poseStack, VertexConsumer vertexConsumer,
//										SensorResult result, Vec3 offset) {
//		AABB smoothBox = result.box().move(offset.x, offset.y, offset.z);
//		float r = result.colliding() ? 1.0F : 0.0F;
//		float g = result.colliding() ? 0.0F : 1.0F;
//		float b = 0.0F;
//		LevelRenderer.renderLineBox(poseStack, vertexConsumer,
//			smoothBox.minX, smoothBox.minY, smoothBox.minZ,
//			smoothBox.maxX, smoothBox.maxY, smoothBox.maxZ,
//			r, g, b, 1.0F);
//	}
//}
