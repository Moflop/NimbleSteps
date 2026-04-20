package mod.arcomit.parkour.v2.content.client.animation.player.modifier;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import mod.arcomit.parkour.ServerConfig;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdaptiveClimbSpeedModifier extends AbstractModifier {

	private final Player player;

	// 你制作动画时的基准速度：1 tick 0.2格
	private final float baseSpeedPerTick = 0.2f;
	private float speed = 1.0f;

	// 采用官方的时间累加概念，但进行彻底的安全隔离
	private float delta = 0.0F;
	private float shiftedDelta = 0.0F;

	public AdaptiveClimbSpeedModifier(Player player) {
		this.player = player;
	}

	@Override
	public void tick(AnimationData state) {
		// 动态计算爬墙速度
		double dy = this.player.getY() - this.player.yo;
		if (dy > 0.01 || dy < -0.01) {
			float currentWallClimbSpeed = ServerConfig.WALL_CLIMB_SPEED.get().floatValue();
			this.speed = currentWallClimbSpeed / baseSpeedPerTick;
		} else {
			this.speed = 0f; // 停在墙上时冻结动画
		}

		float d = 1.0F - this.delta;
		this.delta = 0.0F;
		// 传入 true，代表这是逻辑 tick，绝对禁止触发渲染代码
		this.step(d, state, true);
	}

	@Override
	public void setupAnim(AnimationData state) {
		float d = state.getPartialTick() - this.delta;
		this.delta = state.getPartialTick();
		// 传入 false，代表这是渲染帧，允许更新骨骼
		this.step(d, state, false);
	}

	protected void step(float stepDelta, AnimationData state, boolean isTick) {
		stepDelta *= this.speed;
		stepDelta += this.shiftedDelta;

		// 【核心修复 1】保护现场：记录下真正的 PartialTick！
		float originalPartialTick = state.getPartialTick();

		// 正常处理时间溢出，向下传递 tick
		while (stepDelta > 1.0F) {
			--stepDelta;
			super.tick(state);
		}

		// 设置为我们伪造的变速时间，让子动画能正确变形
		state.setPartialTick(stepDelta);

		// 【核心修复 2】绝不在逻辑 Tick() 中调用渲染代码！杜绝跨线程污染！
		if (!isTick) {
			super.setupAnim(state);
		}

		// 【核心修复 3】恢复现场：强行把原版的 PartialTick 塞回去！
		// 这样排在这个 Modifier 之后的所有动画和过程化代码，都能拿到绝对精准的渲染时间！
		state.setPartialTick(originalPartialTick);

		this.shiftedDelta = stepDelta;
	}
}