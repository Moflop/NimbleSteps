package mod.arcomit.parkour.v2.core.sensor.v3.client.debug;


import com.mojang.brigadier.CommandDispatcher;
import mod.arcomit.parkour.ParkourMod;
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
public class DebugCommandHandler {

	@SubscribeEvent
	public static void registerClientCommands(RegisterClientCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

		dispatcher.register(
			Commands.literal("parkour")
				.then(Commands.literal("debug")
					// /parkour debug wall_slide
					.then(Commands.literal("wall_slide")
						.executes(context -> {
							DebugSensorRenderHandler.DEBUG_TYPE = SensorDebugType.WALL_SLIDE;
							context.getSource().sendSystemMessage(Component.literal("§e[Parkour] §aSensor Debug: ONLY WALL SLIDE"));
							return 1;
						})
					)
					// /parkour debug wall_run
					.then(Commands.literal("wall_run")
						.executes(context -> {
							DebugSensorRenderHandler.DEBUG_TYPE = SensorDebugType.WALL_RUN;
							context.getSource().sendSystemMessage(Component.literal("§e[Parkour] §aSensor Debug: ONLY WALL RUN"));
							return 1;
						})
					)
					// /parkour debug wall_climb
					.then(Commands.literal("wall_climb")
						.executes(context -> {
							DebugSensorRenderHandler.DEBUG_TYPE = SensorDebugType.WALL_CLIMB;
							context.getSource().sendSystemMessage(Component.literal("§e[Parkour] §aSensor Debug: ALL WALL CLIMB"));
							return 1;
						})
					)
					// /parkour debug wall_jump
					.then(Commands.literal("wall_jump")
						.executes(context -> {
							DebugSensorRenderHandler.DEBUG_TYPE = SensorDebugType.WALL_JUMP;
							context.getSource().sendSystemMessage(Component.literal("§e[Parkour] §aSensor Debug: ALL WALL JUMP"));
							return 1;
						})
					)
					// /parkour debug nothing
					.then(Commands.literal("nothing")
						.executes(context -> {
							DebugSensorRenderHandler.DEBUG_TYPE = SensorDebugType.NONE;
							context.getSource().sendSystemMessage(Component.literal("§e[Parkour] §aSensor Debug: HIDDEN ALL"));
							return 1;
						})
					)
				)
		);
	}
}