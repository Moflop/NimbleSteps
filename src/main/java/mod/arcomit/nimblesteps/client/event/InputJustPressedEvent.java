package mod.arcomit.nimblesteps.client.event;

import lombok.Getter;
import mod.arcomit.nimblesteps.client.NsKeyMapping;
import net.neoforged.bus.api.Event;

/**
 * 按键刚输入事件。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
@Getter
public class InputJustPressedEvent extends Event {
	private final NsKeyMapping keyMapping;

	public InputJustPressedEvent(NsKeyMapping keyMapping) {
		this.keyMapping = keyMapping;
	}
}
