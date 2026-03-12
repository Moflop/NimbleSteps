package mod.arcomit.parkour.v2.content.mechanic.stepheight;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 可翻越方块高度处理器。
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class StepHeightIncreaseHandler {
	private static final ResourceLocation STEP_HEIGHT_MODIFIER_ID = ParkourMod.prefix("step_height");
	private static final double VANILLA_STEP_HEIGHT = 0.6; // 原版玩家默认的可翻越方块高度

	@SubscribeEvent
	public static void adjustPlayerStepHeight(PlayerTickEvent.Post event) {
		if (!ServerConfig.enableStepHeightIncrease) {
			return;
		}

		Player player = event.getEntity();
		AttributeInstance movementSpeed = player.getAttribute(Attributes.STEP_HEIGHT);
		if (movementSpeed == null) {
			return;
		}

		// 移除旧的修改器
		movementSpeed.removeModifier(STEP_HEIGHT_MODIFIER_ID);

		double stepHeightBonus;
		if (player.isSprinting() && !player.isSwimming()) {
			stepHeightBonus = ServerConfig.sprintStepHeight - VANILLA_STEP_HEIGHT;
		} else {
			stepHeightBonus = ServerConfig.walkStepHeight - VANILLA_STEP_HEIGHT;
		}

		AttributeModifier walkModifier = new AttributeModifier(
			STEP_HEIGHT_MODIFIER_ID,
			stepHeightBonus,
			AttributeModifier.Operation.ADD_VALUE
		);
		movementSpeed.addTransientModifier(walkModifier);
	}
}
