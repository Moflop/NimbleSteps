package mod.arcomit.parkour.v2.content.client;

import com.mojang.blaze3d.platform.InputConstants;
import mod.arcomit.parkour.v2.content.client.event.InputJustPressedEvent;
import mod.arcomit.parkour.v2.content.client.event.InputReleasedEvent;
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
public class NsKeyMapping extends KeyMapping {

	public NsKeyMapping(String name, InputConstants.Type inputType, int keyCode, String category) {
		super(name, inputType, keyCode, category);
	}

	@Override
	public void setDown(boolean value) {
		if (this.isDown() == value) {
			return;
		}
		if (value) {
			NeoForge.EVENT_BUS.post(new InputJustPressedEvent(this));
		} else {
			NeoForge.EVENT_BUS.post(new InputReleasedEvent(this));
		}
		super.setDown(value);
	}
}
