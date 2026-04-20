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
public class ProceduralWallRunModifier extends AbstractModifier {

	private final Player player;
	private final boolean isWallOnLeft; // 墙在左边还是右边

	private int lastUpdateTick = -1;
	private float partialTick = 0f;

	private float animPhaseO = 0f;
	private float animPhase = 0f;
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
				this.animPhase += 0.6662f;
				this.amplitude = Mth.lerp(0.2f, this.amplitude, 1.0f);
			} else {
				this.amplitude = Mth.lerp(0.2f, this.amplitude, 0f);
			}
		}
	}

	@Override
	public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
		// 此时拿到的骨骼，已经是完美的左墙/右墙 JSON 姿势了
		bone = super.get3DTransform(bone);
		String boneName = bone.getName();

		if (boneName.equals("head")) {
			float pitch = Mth.lerp(this.partialTick, this.player.xRotO, this.player.getXRot());
			float headYaw = Mth.lerp(this.partialTick, this.player.yHeadRotO, this.player.getYHeadRot());
			float bodyYaw = Mth.lerp(this.partialTick, this.player.yBodyRotO, this.player.yBodyRot);
			float netYaw = headYaw - bodyYaw;

			bone.rotX += pitch * Mth.DEG_TO_RAD;
			bone.rotY += netYaw * Mth.DEG_TO_RAD;
			return bone; // 头部处理完直接返回
		}

		if (this.amplitude > 0.001f || this.amplitudeO > 0.001f) {
			float smoothPhase = Mth.lerp(this.partialTick, this.animPhaseO, this.animPhase);
			float smoothAmplitude = Mth.lerp(this.partialTick, this.amplitudeO, this.amplitude);

			// 跑步摆动与贴墙微颤
			float runSwing = Mth.sin(smoothPhase) * smoothAmplitude * 0.8f;
			float wallArmBob = (float) Math.toRadians(-8 * Math.cos(smoothPhase)) * smoothAmplitude;

			switch (boneName) {
				case "left_leg":
					bone.rotX -= runSwing;
					break;
				case "right_leg":
					bone.rotX += runSwing;
					break;
				case "left_arm":
					if (isWallOnLeft) {
						// 墙在左侧，左手是摸墙手，加一点微颤
						bone.rotX += wallArmBob;
					} else {
						// 墙在右侧，左手是悬空手，加跑步摆动
						bone.rotX += runSwing;
					}
					break;
				case "right_arm":
					if (isWallOnLeft) {
						// 墙在左侧，右手是悬空手，加跑步摆动
						bone.rotX -= runSwing;
					} else {
						// 墙在右侧，右手是摸墙手，加一点微颤
						bone.rotX += wallArmBob;
					}
					break;
				case "torso":
					float yaw = (float) Math.toRadians(8 * Math.cos(smoothPhase)) * smoothAmplitude;
					bone.rotY += isWallOnLeft ? -yaw : yaw;
					break;
				case "body":
					float bounce = (float) Math.pow(Math.cos(smoothPhase), 2) * smoothAmplitude * 1.5f;
					bone.positionY += bounce;
					break;
			}
		}
		return bone;
	}
}