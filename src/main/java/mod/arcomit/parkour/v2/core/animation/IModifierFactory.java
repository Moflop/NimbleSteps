package mod.arcomit.parkour.v2.core.animation;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface IModifierFactory {
	/**
	 * @param controller 动画控制器
	 * @param player     客户端玩家
	 * @param state      当前状态实例（如果需要读取内部数据）
	 * @param variant    动画变体
	 */
	void apply(PlayerAnimationController controller, AbstractClientPlayer player, IParkourState state, int variant);
}