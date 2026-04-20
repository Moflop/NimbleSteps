package mod.arcomit.parkour.v2.content.client.animation.player.modifier;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ProceduralLandingRollModifier extends AbstractModifier {

	private final Player player;
	private int currentTick = 0;
	private int lastUpdateTick = -1;
	private float partialTick = 0f;

	private final int totalDuration;     // 翻滚动画的总长度 (Tick)
	private final int fadeOutDuration;   // 最后多少 Tick 开始让原版接管

	/**
	 * @param player          玩家实体
	 * @param totalDuration   翻滚 JSON 动画的总时长 (如 0.8秒 = 16 Tick)
	 * @param fadeOutDuration 期望最后多少 Tick 原版开始介入 (如 6 Tick)
	 */
	public ProceduralLandingRollModifier(Player player, int totalDuration, int fadeOutDuration) {
		this.player = player;
		this.totalDuration = totalDuration;
		this.fadeOutDuration = fadeOutDuration;
	}

	@Override
	public void setupAnim(AnimationData state) {
		super.setupAnim(state);
		this.partialTick = state.getPartialTick();
	}

	@Override
	public void tick(AnimationData state) {
		super.tick(state);
		// 独立的动画 Tick 计数器
		if (this.player.tickCount != this.lastUpdateTick) {
			this.lastUpdateTick = this.player.tickCount;
			this.currentTick++;
		}
	}

	@Override
	public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
		bone = super.get3DTransform(bone);
		String boneName = bone.getName();

//		if (boneName.equals("body")) {
//			float degrees = (float) Math.toDegrees(bone.rotX);
//			float wrappedDegrees = Mth.wrapDegrees(degrees);
//			bone.rotX = wrappedDegrees * ((float) Math.PI / 180f);
//		}

		float smoothTime = this.currentTick + this.partialTick;
		float fadeStartTime = this.totalDuration - this.fadeOutDuration;

		// 如果还没进入过渡期，或者动画已经完全结束，直接返回原始骨骼
		if (smoothTime < fadeStartTime) {
			return bone;
		}

		// 计算原版接管的权重 [0.0 -> 1.0]
		// 0.0 表示完全跟随翻滚 JSON，1.0 表示完全跟随原版视角和步伐
		float vanillaWeight = Mth.clamp((smoothTime - fadeStartTime) / this.fadeOutDuration, 0f, 1f);

		if (boneName.equals("head")) {
			float pitch = Mth.lerp(this.partialTick, this.player.xRotO, this.player.getXRot());
			float headYaw = Mth.lerp(this.partialTick, this.player.yHeadRotO, this.player.getYHeadRot());
			float bodyYaw = Mth.lerp(this.partialTick, this.player.yBodyRotO, this.player.yBodyRot);
			float netYaw = headYaw - bodyYaw;

			float targetRotX = pitch * ((float) Math.PI / 180f);
			float targetRotY = netYaw * ((float) Math.PI / 180f);
			float targetRotZ = 0f;

			// 使用自定义的最短路径弧度插值
			bone.rotX = rotLerpRadians(vanillaWeight, bone.rotX, targetRotX);
			bone.rotY = rotLerpRadians(vanillaWeight, bone.rotY, targetRotY);
			bone.rotZ = rotLerpRadians(vanillaWeight, bone.rotZ, targetRotZ);
		}

		// ==========================================
		// 2. 四肢走路/跑步步态接管
		// ==========================================
		boolean isArmOrLeg = boneName.equals("left_arm") || boneName.equals("right_arm") ||
			boneName.equals("left_leg") || boneName.equals("right_leg");

		if (isArmOrLeg) {
			// 直接读取原版引擎计算好的步行相位和速度
			float swingAmount = this.player.walkAnimation.speed(this.partialTick);
			float swingProgress = this.player.walkAnimation.position(this.partialTick);

			// 模拟原版 Minecraft 的四肢摆动公式
			float legSwing = Mth.cos(swingProgress * 0.6662f) * 1.4f * swingAmount;
			float armSwing = Mth.cos(swingProgress * 0.6662f + (float) Math.PI) * 2.0f * swingAmount * 0.5f;

			float targetRotX = 0f;
			switch (boneName) {
				case "left_leg":  targetRotX = -legSwing; break;
				case "right_leg": targetRotX = legSwing; break;
				case "left_arm":  targetRotX = -armSwing; break;
				case "right_arm": targetRotX = armSwing; break;
			}

			// 平滑接管 X 轴（前后摆动）
			bone.rotX = Mth.lerp(vanillaWeight, bone.rotX, targetRotX);
			// 强行把四肢被 JSON 扭曲的 Y/Z 轴复位回 0（原版默认垂直下垂）
			bone.rotY = Mth.lerp(vanillaWeight, bone.rotY, 0f);
			bone.rotZ = Mth.lerp(vanillaWeight, bone.rotZ, 0f);
		}

		return bone;
	}

	/**
	 * 弧度的最短路径插值，确保旋转差值限制在 -π 到 π 之间（即 -180 到 180 度）
	 */
	private float rotLerpRadians(float delta, float start, float end) {
		float diff = end - start;

		// 将差值规范化到 -PI 到 PI 的范围内
		while (diff < -(float)Math.PI) {
			diff += (float)Math.PI * 2f;
		}
		while (diff >= (float)Math.PI) {
			diff -= (float)Math.PI * 2f;
		}

		return start + delta * diff;
	}
}