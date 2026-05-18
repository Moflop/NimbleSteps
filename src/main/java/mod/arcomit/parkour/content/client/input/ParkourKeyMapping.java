package mod.arcomit.parkour.content.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;
import mod.arcomit.parkour.content.client.event.InputJustPressedEvent;
import mod.arcomit.parkour.content.client.event.InputReleasedEvent;
import mod.arcomit.parkour.core.input.ParkourInputActions;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;

/**
 * 客户端按键映射类。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
@OnlyIn(Dist.CLIENT)
public class ParkourKeyMapping extends KeyMapping {
	private int pressedTicks;
	@Getter
	private ParkourInputActions inputAction = ParkourInputActions.NONE;

	public ParkourKeyMapping(String name, InputConstants.Type inputType, int keyCode, String category, ParkourInputActions inputAction) {
		super(name, inputType, keyCode, category);
		this.inputAction = inputAction;
	}

	@Override
	public void setDown(boolean value) {
		if (this.isDown() == value) {
			if (this.isDown()) {
				pressedTicks++;
			}
			return;
		}
		if (value) {
			NeoForge.EVENT_BUS.post(new InputJustPressedEvent(this));
		} else {
			NeoForge.EVENT_BUS.post(new InputReleasedEvent(this, pressedTicks));
			pressedTicks = 0;
		}
		super.setDown(value);
	}
}
