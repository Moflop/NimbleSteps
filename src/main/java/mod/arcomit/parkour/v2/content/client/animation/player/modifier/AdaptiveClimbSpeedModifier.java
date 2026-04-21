package mod.arcomit.parkour.v2.content.client.animation.player.modifier;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import mod.arcomit.parkour.ServerConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class AdaptiveClimbSpeedModifier extends AbstractModifier {

	private final Player player;

	private final float baseSpeedPerTick = 0.2f;
	private float speed = 1.0f;

	private float delta = 0.0F;
	private float shiftedDelta = 0.0F;

	// 只保留这一个变量，用来精准计算原版引擎的渲染时间
	private float renderPartialTick = 0f;

	public AdaptiveClimbSpeedModifier(Player player) {
		this.player = player;
	}

	@Override
	public void tick(AnimationData state) {
		double dy = this.player.getY() - this.player.yo;
		if (dy > 0.01 || dy < -0.01) {
			float currentWallClimbSpeed = ServerConfig.WALL_CLIMB_SPEED.get().floatValue();
			this.speed = currentWallClimbSpeed / baseSpeedPerTick;
		} else {
			this.speed = 0f;
		}

		float d = 1.0F - this.delta;
		this.delta = 0.0F;
		this.step(d, state, true);
	}

	@Override
	public void setupAnim(AnimationData state) {
		// 记录真实的渲染插值时间
		this.renderPartialTick = state.getPartialTick();

		float d = state.getPartialTick() - this.delta;
		this.delta = state.getPartialTick();
		this.step(d, state, false);
	}

	protected void step(float stepDelta, AnimationData state, boolean isTick) {
		stepDelta *= this.speed;
		stepDelta += this.shiftedDelta;

		float originalPartialTick = state.getPartialTick();

		while (stepDelta > 1.0F) {
			--stepDelta;
			super.tick(state);
		}

		state.setPartialTick(stepDelta);

		if (!isTick) {
			super.setupAnim(state);
		}

		state.setPartialTick(originalPartialTick);
		this.shiftedDelta = stepDelta;
	}

	// ================= 极简版：根据 getDirection() 锁死身体 =================
	@Override
	public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
		bone = super.get3DTransform(bone);
		String boneName = bone.getName();

		// 我们只关心需要旋转的身体和头部
		if (boneName.equals("body")) {

			// 1. 获取 Minecraft 引擎原本打算把身体转到的角度
			float currentVanillaBodyYaw = Mth.lerp(this.renderPartialTick, this.player.yBodyRotO, this.player.yBodyRot);

			// 2. 获取玩家绝对面朝方向（东南西北）对应的角度
			float targetYaw = this.player.getDirection().toYRot();

			// 3. 计算需要补偿的差值
			float yawDiff = targetYaw - currentVanillaBodyYaw;

			bone.rotY += yawDiff * Mth.DEG_TO_RAD;
		}

		return bone;
	}
}