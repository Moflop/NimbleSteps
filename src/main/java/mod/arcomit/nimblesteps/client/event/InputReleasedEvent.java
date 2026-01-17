package mod.arcomit.nimblesteps.client.event;

import lombok.Getter;
import mod.arcomit.nimblesteps.client.NsKeyMapping;
import net.neoforged.bus.api.Event;

/**
 * 按键释放事件。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
@Getter
public class InputReleasedEvent extends Event {
	private final NsKeyMapping keyMapping;

	public InputReleasedEvent(NsKeyMapping keyMapping) {
		this.keyMapping = keyMapping;
	}
}
