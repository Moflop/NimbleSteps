package mod.arcomit.parkour.v2.core.sensor.v3.client.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.sensor.v3.impl.HeadFeetSensor;
import mod.arcomit.parkour.v2.core.sensor.v3.impl.JumpWallSensor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
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
 * 调试传感器渲染处理器
 * 用于在游戏中可视化不同动作传感器（滑墙、跑墙、爬墙、蹬墙跳）的碰撞体积与状态
 *
 * @author Arcomit
 * @since 2026-05-15
 */
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class DebugSensorRenderHandler {

	public static SensorDebugType DEBUG_TYPE = SensorDebugType.NONE;

	/** 四个水平方向常量，避免重复创建列表 */
	private static final List<Direction> HORIZONTALS = List.of(
		Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
	);

	/**
	 * 碰撞检测函数式接口
	 */
	@FunctionalInterface
	private interface CollisionChecker {
		boolean test(Player player, Direction direction);
	}

	/**
	 * 碰撞箱获取函数式接口
	 */
	@FunctionalInterface
	private interface BoxProvider {
		List<AABB> get(Player player, Direction direction);
	}

	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
		if (DEBUG_TYPE == SensorDebugType.NONE) return;

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) return;

		// 准备渲染所需的基础对象
		PoseStack poseStack = event.getPoseStack();
		Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();
		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

		// 计算平滑位置偏移（仅计算一次）
		float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
		double smoothX = Mth.lerp(partialTick, player.xo, player.getX());
		double smoothY = Mth.lerp(partialTick, player.yo, player.getY());
		double smoothZ = Mth.lerp(partialTick, player.zo, player.getZ());
		Vec3 logicPos = player.position();
		double offsetX = smoothX - logicPos.x;
		double offsetY = smoothY - logicPos.y;
		double offsetZ = smoothZ - logicPos.z;

		poseStack.pushPose();
		poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

		// 根据调试类型选择传感器实现与需要检测的方向
		CollisionChecker checker;
		BoxProvider provider;
		List<Direction> directions;

		switch (DEBUG_TYPE) {
			case WALL_SLIDE:
				checker = HeadFeetSensor::isColliding;
				provider = HeadFeetSensor::getBoxes;
				directions = HORIZONTALS;
				break;
			case WALL_RUN: {
				Direction facing = player.getDirection();
				checker = HeadFeetSensor::isColliding;
				provider = HeadFeetSensor::getBoxes;
				directions = List.of(facing.getCounterClockWise(), facing.getClockWise());
				break;
			}
			case WALL_CLIMB:
				checker = HeadFeetSensor::isColliding;
				provider = HeadFeetSensor::getBoxes;
				directions = List.of(player.getDirection());
				break;
			case WALL_JUMP:
				checker = JumpWallSensor::isColliding;
				provider = JumpWallSensor::getBoxes;
				directions = HORIZONTALS;
				break;
			default:
				poseStack.popPose();
				return;
		}

		// 统一渲染所有指定方向的碰撞箱
		for (Direction dir : directions) {
			renderDirectionBoxes(player, dir, checker, provider,
				poseStack, vertexConsumer, offsetX, offsetY, offsetZ);
		}

		// 结束线条渲染批次，确保所有绘制立即提交
		bufferSource.endBatch(RenderType.lines());

		poseStack.popPose();
	}

	/**
	 * 对单个方向进行碰撞检测并渲染所有相关包围盒
	 */
	private static void renderDirectionBoxes(Player player, Direction direction,
	                                         CollisionChecker checker, BoxProvider provider,
	                                         PoseStack poseStack, VertexConsumer vertexConsumer,
	                                         double offsetX, double offsetY, double offsetZ) {
		boolean colliding = checker.test(player, direction);
		// 碰撞时显示红色，未碰撞时显示绿色，蓝色通道保持0
		float r = colliding ? 1.0F : 0.0F;
		float g = colliding ? 0.0F : 1.0F;
		float b = 0.0F;

		for (AABB box : provider.get(player, direction)) {
			AABB smoothBox = box.move(offsetX, offsetY, offsetZ);
			LevelRenderer.renderLineBox(poseStack, vertexConsumer,
				smoothBox.minX, smoothBox.minY, smoothBox.minZ,
				smoothBox.maxX, smoothBox.maxY, smoothBox.maxZ,
				r, g, b, 1.0F);
		}
	}
}