//package mod.arcomit.parkour.v2.core.sensor.v3.client.debug;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import mod.arcomit.parkour.ParkourMod;
//import mod.arcomit.parkour.v2.core.sensor.v3.impl.HeadFeetSensor;
//import mod.arcomit.parkour.v2.core.sensor.v3.impl.JumpWallSensor;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.LevelRenderer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.core.Direction;
//import net.minecraft.util.Mth;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.phys.AABB;
//import net.minecraft.world.phys.Vec3;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
//
///**
// * TODO：描述
// *
// * @author Arcomit
// * @since 2026-05-15
// */
//@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
//public class DebugSensorRenderHandlerV2 {
//	public static SensorDebugType DEBUG_TYPE = SensorDebugType.NONE;
//
//	@SubscribeEvent
//	public static void onRenderLevelStage(RenderLevelStageEvent event) {
//		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
//
//		Minecraft mc = Minecraft.getInstance();
//		Player player = mc.player;
//		if (player == null) return;
//
//		PoseStack poseStack = event.getPoseStack();
//		Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();
//
//		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
//		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
//
//		poseStack.pushPose();
//		poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
//
//		Vec3 logicPos = player.position();
//		float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
//		double smoothX = Mth.lerp(partialTick, player.xo, player.getX());
//		double smoothY = Mth.lerp(partialTick, player.yo, player.getY());
//		double smoothZ = Mth.lerp(partialTick, player.zo, player.getZ());
//
//		double offsetX = smoothX - logicPos.x;
//		double offsetY = smoothY - logicPos.y;
//		double offsetZ = smoothZ - logicPos.z;
//
//		if (DEBUG_TYPE == SensorDebugType.WALL_SLIDE) {
//			for (Direction direction : Direction.Plane.HORIZONTAL) {
//				boolean isColliding = HeadFeetSensor.isColliding(player, direction);
//				float r = isColliding ? 1.0F : 0.0F;
//				float g = isColliding ? 0.0F : 1.0F;
//				float b = 0.0F;
//				for (AABB box : HeadFeetSensor.getBoxes(player, direction)) {
//					AABB smoothBox = box.move(offsetX, offsetY, offsetZ);
//					LevelRenderer.renderLineBox(
//						poseStack, vertexConsumer,
//						smoothBox.minX, smoothBox.minY, smoothBox.minZ,
//						smoothBox.maxX, smoothBox.maxY, smoothBox.maxZ,
//						r, g, b, 1.0F
//					);
//				}
//			}
//		}else if (DEBUG_TYPE == SensorDebugType.WALL_RUN) {
//			Direction facing = player.getDirection();
//			Direction left = facing.getCounterClockWise();
//			Direction right = facing.getClockWise();
//			boolean isLeftColliding = HeadFeetSensor.isColliding(player, left);
//			float r = isLeftColliding ? 1.0F : 0.0F;
//			float g = isLeftColliding ? 0.0F : 1.0F;
//			float b = 0.0F;
//			for (AABB box : HeadFeetSensor.getBoxes(player, left)) {
//				AABB smoothBox = box.move(offsetX, offsetY, offsetZ);
//				LevelRenderer.renderLineBox(
//					poseStack, vertexConsumer,
//					smoothBox.minX, smoothBox.minY, smoothBox.minZ,
//					smoothBox.maxX, smoothBox.maxY, smoothBox.maxZ,
//					r, g, b, 1.0F
//				);
//			}
//			boolean isRightColliding = HeadFeetSensor.isColliding(player, right);
//			float r2 = isRightColliding ? 1.0F : 0.0F;
//			float g2 = isRightColliding ? 0.0F : 1.0F;
//			float b2 = 0.0F;
//			for (AABB box : HeadFeetSensor.getBoxes(player, right)) {
//				AABB smoothBox = box.move(offsetX, offsetY, offsetZ);
//				LevelRenderer.renderLineBox(
//					poseStack, vertexConsumer,
//					smoothBox.minX, smoothBox.minY, smoothBox.minZ,
//					smoothBox.maxX, smoothBox.maxY, smoothBox.maxZ,
//					r2, g2, b2, 1.0F
//				);
//			}
//		}else if (DEBUG_TYPE == SensorDebugType.WALL_CLIMB) {
//			Direction facing = player.getDirection();
//			boolean isColliding = HeadFeetSensor.isColliding(player, facing);
//			float r = isColliding ? 1.0F : 0.0F;
//			float g = isColliding ? 0.0F : 1.0F;
//			float b = 0.0F;
//			for (AABB box : HeadFeetSensor.getBoxes(player, facing)) {
//				AABB smoothBox = box.move(offsetX, offsetY, offsetZ);
//				LevelRenderer.renderLineBox(
//					poseStack, vertexConsumer,
//					smoothBox.minX, smoothBox.minY, smoothBox.minZ,
//					smoothBox.maxX, smoothBox.maxY, smoothBox.maxZ,
//					r, g, b, 1.0F
//				);
//			}
//		}else if (DEBUG_TYPE == SensorDebugType.WALL_JUMP) {
//			for (Direction direction : Direction.Plane.HORIZONTAL) {
//				boolean isColliding = JumpWallSensor.isColliding(player, direction);
//				float r = isColliding ? 1.0F : 0.0F;
//				float g = isColliding ? 0.0F : 1.0F;
//				float b = 0.0F;
//				for (AABB box : JumpWallSensor.getBoxes(player, direction)) {
//					AABB smoothBox = box.move(offsetX, offsetY, offsetZ);
//					LevelRenderer.renderLineBox(
//						poseStack, vertexConsumer,
//						smoothBox.minX, smoothBox.minY, smoothBox.minZ,
//						smoothBox.maxX, smoothBox.maxY, smoothBox.maxZ,
//						r, g, b, 1.0F
//					);
//				}
//			}
//		}
//
//		poseStack.popPose();
//	}
//}
