package mod.arcomit.parkour.v2.content.client.command;

import com.mojang.brigadier.CommandDispatcher;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.sensor.SensorDebugRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

/**
 * 客户端指令：用于控制 Sensor Debug 渲染
 *
 * @author Arcomit
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ParkourMod.MODID, value = Dist.CLIENT)
public class DebugCommand {

	@SubscribeEvent
	public static void registerClientCommands(RegisterClientCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

		dispatcher.register(
			Commands.literal("nimblesteps")
				.then(Commands.literal("debug")
					// 1. /nimblesteps debug -> 切换全局开关
					.executes(context -> {
						SensorDebugRenderer.RENDER_DEBUG_BOXES = !SensorDebugRenderer.RENDER_DEBUG_BOXES;
						boolean state = SensorDebugRenderer.RENDER_DEBUG_BOXES;
						context.getSource().sendSystemMessage(Component.literal("§e[NimbleSteps] §aSensor Debug: " + (state ? "§2ON" : "§cOFF")));
						return 1;
					})
					// 2. /nimblesteps debug wall -> 仅渲染 HeadWall 和 FeetWall
					.then(Commands.literal("wall")
						.executes(context -> {
							SensorDebugRenderer.RENDER_WALL = true;
							SensorDebugRenderer.RENDER_JUMP = false;
							SensorDebugRenderer.RENDER_ARMHANG = false;
							context.getSource().sendSystemMessage(Component.literal("§e[NimbleSteps] §aSensor Debug: ONLY WALL"));
							return 1;
						})
					)
					// 3. /nimblesteps debug jump -> 仅渲染 JumpWall
					.then(Commands.literal("jump")
						.executes(context -> {
							SensorDebugRenderer.RENDER_WALL = false;
							SensorDebugRenderer.RENDER_JUMP = true;
							SensorDebugRenderer.RENDER_ARMHANG = false;
							context.getSource().sendSystemMessage(Component.literal("§e[NimbleSteps] §aSensor Debug: ONLY JUMP"));
							return 1;
						})
					)
					// 4. /nimblesteps debug armhang -> 仅渲染 Armhang 系列
					.then(Commands.literal("armhang")
						.executes(context -> {
							SensorDebugRenderer.RENDER_WALL = false;
							SensorDebugRenderer.RENDER_JUMP = false;
							SensorDebugRenderer.RENDER_ARMHANG = true;
							context.getSource().sendSystemMessage(Component.literal("§e[NimbleSteps] §aSensor Debug: ONLY ARMHANG"));
							return 1;
						})
					)
					// 5. /nimblesteps debug all -> 渲染所有
					.then(Commands.literal("all")
						.executes(context -> {
							SensorDebugRenderer.RENDER_WALL = true;
							SensorDebugRenderer.RENDER_JUMP = true;
							SensorDebugRenderer.RENDER_ARMHANG = true;
							context.getSource().sendSystemMessage(Component.literal("§e[NimbleSteps] §aSensor Debug: ALL SENSORS"));
							return 1;
						})
					)
					// 6. /nimblesteps debug nothing -> 关闭所有渲染
					.then(Commands.literal("nothing")
						.executes(context -> {
							SensorDebugRenderer.RENDER_WALL = false;
							SensorDebugRenderer.RENDER_JUMP = false;
							SensorDebugRenderer.RENDER_ARMHANG = false;
							context.getSource().sendSystemMessage(Component.literal("§e[NimbleSteps] §aSensor Debug: HIDDEN ALL"));
							return 1;
						})
					)
				)
		);
	}
}