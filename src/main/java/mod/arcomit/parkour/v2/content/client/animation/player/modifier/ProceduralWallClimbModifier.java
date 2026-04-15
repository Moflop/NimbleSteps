package mod.arcomit.parkour.v2.content.client.animation.player.modifier;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ProceduralWallClimbModifier extends AbstractModifier {

	private final Player player;
	private final float animationLengthTicks = 16f; // 你 JSON 里的 0.8秒 对应的逻辑刻是 16 Tick

	private int lastUpdateTick = -1;
	private float partialTick = 0f;

	// 自己维护的时间轴，用于同步 JSON 动画和物理数学曲线
	private float animTimeO = 0f;
	private float animTime = 0f;

	private float amplitude = 0f;

	public ProceduralWallClimbModifier(Player player) {
		this.player = player;
	}

	@Override
	public void setupAnim(AnimationData state) {
		super.setupAnim(state);
		this.partialTick = state.getPartialTick();
	}

	@Override
	public void tick(AnimationData state) {
		super.tick(state);

		if (this.player.tickCount != this.lastUpdateTick) {
			this.lastUpdateTick = this.player.tickCount;
			this.animTimeO = this.animTime;

			float movementIntensity = 0f;
			// 抛弃物理 dx/dz，读取键盘事件，免疫撞墙卡顿
			if (this.player instanceof LocalPlayer localPlayer) {
				movementIntensity = Math.max(Math.abs(localPlayer.input.forwardImpulse), Math.abs(localPlayer.input.leftImpulse));
			} else {
				double dx = this.player.getX() - this.player.xo;
				double dz = this.player.getZ() - this.player.zo;
				movementIntensity = (float) Math.sqrt(dx * dx + dz * dz) * 10.0f;
			}

			if (movementIntensity > 0.05f) {
				// 推进时间轴。这里的增加速度必须与你 JSON 循环长度严格匹配！
				// JSON 是 0.8s 循环，这里每 Tick(0.05s) 增加 M_PI / (16) 左右，
				// 保证 16 Tick 后刚好完成一个正弦波周期。
				this.animTime += 0.3927f; // (PI / 8)
				this.amplitude = Mth.lerp(0.3f, this.amplitude, 1.0f);
			} else {
				this.amplitude = Mth.lerp(0.3f, this.amplitude, 0f);
			}
		}
	}

	@Override
	public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
		// 先获取你在 JSON 里做好的基础爬墙姿势（双手交替和双腿交替）
		bone = super.get3DTransform(bone);

		if (this.amplitude > 0.001f) {

			// 绝对丝滑的帧插值
			float smoothTime = Mth.lerp(this.partialTick, this.animTimeO, this.animTime);

			switch (bone.getName()) {
				case "body":
					// 1. 【重力灵魂】ParCool 究极复刻的 cos^2(t) 弹跳曲线
					// 每当脚蹬墙一次，这里会产生一个向上 2 格的扎实反弹，动作瞬间有了重量感！
					// 为了同步你的 JSON 步伐节奏，我们将相位旋转 PI/2
					float bouncePhase = smoothTime + ((float) Math.PI / 2f);
					float bounce = (float) Math.pow(Math.cos(bouncePhase), 2) * this.amplitude * 2.0f;
					bone.positionY += bounce;
					break;
				case "torso":
					// 2. 身体节奏性扭转
					// 躯干随着双手交替产生 8 度左右的 Yaw 轴扭转，增加张力
					float yaw = (float) Math.toRadians(8 * Math.cos(smoothTime)) * this.amplitude;
					bone.rotY += yaw;
					break;

				case "left_arm":
					// 进阶优化：在 JSON 摆臂基础上，增加一点点 Z 轴摩擦动作
					float armFriction = Mth.sin(smoothTime) * this.amplitude * 0.1f;
					bone.rotZ -= armFriction;
					break;
				case "right_arm":
					float armFrictionR = Mth.sin(smoothTime) * this.amplitude * 0.1f;
					bone.rotZ += armFrictionR;
					break;
			}
		}
		return bone;
	}
}