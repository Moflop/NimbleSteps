package mod.arcomit.parkour.v2.content.mechanic.movespeed;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.ParkourMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 移动加速处理器。
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class MoveSpeedIncreaseHandler {
	private static final ResourceLocation SPEED_MULTIPLIER_MODIFIER_ID = ParkourMod.prefix("speed_multiplier");
	private static final double VANILLA_SPEED_MULTIPLIER = 1.0; // 原版玩家默认的速度倍率

	@SubscribeEvent
	public static void adjustPlayerSpeed(PlayerTickEvent.Post event) {
		if (!ServerConfig.enableMoveSpeedIncrease) {
			return;
		}

		Player player = event.getEntity();
		AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
		if (movementSpeed == null) {
			return;
		}

		// 移除旧的修改器
		movementSpeed.removeModifier(SPEED_MULTIPLIER_MODIFIER_ID);

		double speedBonus;
		if (player.isSprinting()) {
			speedBonus = ServerConfig.sprintSpeedMultiplier - VANILLA_SPEED_MULTIPLIER;
		} else {
			speedBonus = ServerConfig.walkSpeedMultiplier - VANILLA_SPEED_MULTIPLIER;
		}

		AttributeModifier walkModifier = new AttributeModifier(
			SPEED_MULTIPLIER_MODIFIER_ID,
			speedBonus,
			AttributeModifier.Operation.ADD_MULTIPLIED_BASE
		);
		movementSpeed.addTransientModifier(walkModifier);
	}
}
