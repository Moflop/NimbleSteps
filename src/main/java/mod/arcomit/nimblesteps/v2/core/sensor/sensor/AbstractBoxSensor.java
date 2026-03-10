package mod.arcomit.nimblesteps.v2.core.sensor.sensor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public abstract class AbstractBoxSensor {
	private final String id;

	// 缓存数据
	private int lastUpdateTick = -1;
	private AABB currentWorldBox = new AABB(0,0,0,0,0,0);
	private boolean isColliding = false;

	public AbstractBoxSensor(String id) {
		this.id = id;
	}

	/**
	 * 核心逻辑：按需更新（惰性求值）
	 */
	private void updateIfNeeded(Player player) {
		// 如果当前的 tick 已经计算过，就直接复用缓存，不再做重复的物理检测
		if (this.lastUpdateTick != player.tickCount) {
			this.currentWorldBox = calculateWorldBox(player);
			this.isColliding = checkCollision(player, this.currentWorldBox);
			this.lastUpdateTick = player.tickCount;
		}
	}

	// 获取碰撞结果时，触发按需更新
	public boolean isColliding(Player player) {
		updateIfNeeded(player);
		return isColliding;
	}

	// 获取碰撞箱（给 Debug 渲染器用）时，触发按需更新
	public AABB getCurrentWorldBox(Player player) {
		updateIfNeeded(player);
		return currentWorldBox;
	}

	public String getId() { return id; }

	// 由子类实现计算逻辑
	protected abstract AABB calculateWorldBox(Player player);

	protected abstract boolean shouldDebugRender(Player player);

	protected boolean checkCollision(Player player, AABB box) {
		return !player.level().noCollision(player, box);
	}
}