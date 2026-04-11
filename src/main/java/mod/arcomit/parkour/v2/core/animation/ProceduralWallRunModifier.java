package mod.arcomit.parkour.v2.core.animation;

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
public class ProceduralWallRunModifier extends AbstractModifier {

	private final Player player;
	private final boolean isWallOnLeft; // 墙在左边还是右边

	private int lastUpdateTick = -1;
	private float partialTick = 0f;

	// 双线程平滑时间轴
	private float animPhaseO = 0f;
	private float animPhase = 0f;

	// 振幅（控制动画张力）
	private float amplitudeO = 0f;
	private float amplitude = 0f;

	public ProceduralWallRunModifier(Player player, boolean isWallOnLeft) {
		this.player = player;
		this.isWallOnLeft = isWallOnLeft;
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

			this.animPhaseO = this.animPhase;
			this.amplitudeO = this.amplitude;

			float movementIntensity = 0f;

			if (this.player instanceof LocalPlayer localPlayer) {
				movementIntensity = Math.max(Math.abs(localPlayer.input.forwardImpulse), Math.abs(localPlayer.input.leftImpulse));
			} else {
				double dx = this.player.getX() - this.player.xo;
				double dz = this.player.getZ() - this.player.zo;
				movementIntensity = (float) Math.sqrt(dx * dx + dz * dz) * 10.0f;
			}

			if (movementIntensity > 0.05f) {
				// 【复刻 ParCool】：ParCool 使用 limbSwing * 0.6662f 作为步频相位
				this.animPhase += 0.6662f;
				this.amplitude = Mth.lerp(0.2f, this.amplitude, 1.0f);
			} else {
				this.amplitude = Mth.lerp(0.2f, this.amplitude, 0f);
			}
		}
	}

	@Override
	public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
		// 先获取你在 Blockbench 里做好的基础贴墙姿势
		bone = super.get3DTransform(bone);

		// ==========================================
		// 1. 头部视角跟随：永远生效！无视玩家是否在移动
		// ==========================================
		if (bone.getName().equals("head")) {
			float pitch = Mth.lerp(this.partialTick, this.player.xRotO, this.player.getXRot());
			float headYaw = Mth.lerp(this.partialTick, this.player.yHeadRotO, this.player.getYHeadRot());
			float bodyYaw = Mth.lerp(this.partialTick, this.player.yBodyRotO, this.player.yBodyRot);
			float netYaw = headYaw - bodyYaw;

			bone.rotX += pitch * ((float) Math.PI / 180f);
			bone.rotY += netYaw * ((float) Math.PI / 180f);
		}

		if (this.amplitude > 0.001f || this.amplitudeO > 0.001f) {

			// 绝对丝滑的插值
			float smoothPhase = Mth.lerp(this.partialTick, this.animPhaseO, this.animPhase);
			float smoothAmplitude = Mth.lerp(this.partialTick, this.amplitudeO, this.amplitude);

			// ==========================================
			// 【完全复刻 ParCool 的动作曲线公式】
			// ==========================================

			// 1. 正常的腿部/悬空臂摆动 (Sin曲线)
			float runSwing = Mth.sin(smoothPhase) * smoothAmplitude * 0.8f; // 0.8弧度约为45度摆幅

			// 2. 贴墙手的微弱摩擦震动 (Cos曲线)
			// ParCool 源码：20 - 8d * Math.cos(armSwingPhase)
			// 因为你已经在 Blockbench 里做好了基础贴墙角度，我们这里只附加 8 度的微颤
			float wallArmBob = (float) Math.toRadians(-8 * Math.cos(smoothPhase)) * smoothAmplitude;

			switch (bone.getName()) {
				case "left_leg":
					bone.rotX -= runSwing;
					break;
				case "right_leg":
					bone.rotX += runSwing;
					break;
				case "left_arm":
					if (isWallOnLeft) {
						// 墙在左边：左手只做轻微颤动 (摩擦墙壁)
						bone.rotX += wallArmBob;
					} else {
						// 墙在右边：左手悬空，大幅度冲刺摆动
						bone.rotX += runSwing;
					}
					break;
				case "right_arm":
					if (isWallOnLeft) {
						bone.rotX -= runSwing; // 右手悬空
					} else {
						bone.rotX += wallArmBob; // 右手贴墙
					}
					break;
				case "torso":
					// 4. ParCool 的身体节奏性扭动
					// 源码：rotateYawRightward(sign * (-5f + 8f * Mth.cos(limbSwing * 0.66662f)))
					float yaw = (float) Math.toRadians(8 * Math.cos(smoothPhase)) * smoothAmplitude;
					bone.rotY += isWallOnLeft ? -yaw : yaw;
					break;
				case "body":
					// 3. 【核心灵魂】ParCool 的 cos^2 弹跳计算
					// 源码：0.145f * Math.pow(Math.cos(limbSwing * 0.6662), 2.)
					// 这样每跑一步，玩家躯干都会有一个极具重量感的上下顿挫！
					float bounce = (float) Math.pow(Math.cos(smoothPhase), 2) * smoothAmplitude * 1.5f;
					bone.positionY += bounce;
					break;
			}
		}
		return bone;
	}
}