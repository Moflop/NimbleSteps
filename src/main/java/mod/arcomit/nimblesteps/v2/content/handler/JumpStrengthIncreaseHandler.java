package mod.arcomit.nimblesteps.v2.content.handler;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 跳跃强度增强
 *
 * @author Arcomit
 * @since 2026-01-04
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class JumpStrengthIncreaseHandler {
	private static final ResourceLocation JUMP_STRENGTH_ADD_MODIFIER_ID = NimbleStepsMod.prefix("jump_strength_add");
	private static final double ADD_JUMP_STRENGTH = 0.01; // 原版玩家默认的跳跃强度为0.42

	@SubscribeEvent
	public static void adjustPlayerSpeed(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		AttributeInstance jumpStrength = player.getAttribute(Attributes.JUMP_STRENGTH);
		if (jumpStrength == null) {
			return;
		}

		// 移除旧的修改器
		jumpStrength.removeModifier(JUMP_STRENGTH_ADD_MODIFIER_ID);

		AttributeModifier jumpStrengthModifier = new AttributeModifier(
			JUMP_STRENGTH_ADD_MODIFIER_ID,
			ADD_JUMP_STRENGTH,
			AttributeModifier.Operation.ADD_VALUE
		);
		jumpStrength.addTransientModifier(jumpStrengthModifier);
	}
}