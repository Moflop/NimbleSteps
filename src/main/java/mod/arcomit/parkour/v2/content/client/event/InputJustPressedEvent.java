package mod.arcomit.parkour.v2.content.client.event;

import lombok.Getter;
import mod.arcomit.parkour.v2.content.client.NsKeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;

/**
 * 按键刚输入事件。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
@OnlyIn(Dist.CLIENT)
@Getter
public class InputJustPressedEvent extends Event {
	private final NsKeyMapping keyMapping;

	public InputJustPressedEvent(NsKeyMapping keyMapping) {
		this.keyMapping = keyMapping;
	}
}
