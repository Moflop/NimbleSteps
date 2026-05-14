package mod.arcomit.parkour.v2.core.sensor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public interface ISensor {
	String getId();
	boolean isColliding(Player player);
	AABB getCurrentWorldBox(Player player);
	boolean shouldDebugRender(Player player);
}