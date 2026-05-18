package mod.arcomit.parkour.core.sensor;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollisionCache {
	@Getter @Setter
	private long tick = -1;
	@Getter @Setter
	private Vec3 position;
	private List<AABB> collisionBoxes;  // 重命名
	@Getter @Setter
	private boolean collided;

	public void setCollisionBoxes(List<AABB> boxes) {
		this.collisionBoxes = boxes == null ? null : new ArrayList<>(boxes);
	}

	public List<AABB> getCollisionBoxes() {
		return collisionBoxes == null ? null : Collections.unmodifiableList(collisionBoxes);
	}

	public boolean isValidTick(long tick) {
		return this.tick == tick;
	}

	public boolean isValidPosition(Vec3 currentPosition) {
		if (this.position == null) {
			return false;
		}
		return this.position.equals(currentPosition);
	}
}