package mod.arcomit.parkour.v2.core.animation.player.modifier;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.WallData;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ProceduralWallSlideModifier extends AbstractModifier {

	private final Player player;
	private float partialTick = 0f;

	// 动画状态机变量
	private boolean isFirstTick = true;      // 【保留】首帧判定，解决起步手臂乱飞
	private boolean isBackPose = false;
	private int lastTickCount = 0;
	private float prevBackWeight = 0f;
	private float currentBackWeight = 0f;

	public ProceduralWallSlideModifier(Player player) {
		this.player = player;
	}

	@Override
	public void setupAnim(AnimationData state) {
		super.setupAnim(state);
		this.partialTick = state.getPartialTick();

		if (this.player.tickCount != this.lastTickCount) {
			this.lastTickCount = this.player.tickCount;
			this.prevBackWeight = this.currentBackWeight;

			WallData wallData = ParkourContext.get(this.player).wallData();
			int dirIndex = wallData.getWallSlideDirection();
			if (dirIndex >= 0 && dirIndex <= 5) {
				Direction wallDir = Direction.from3DDataValue(dirIndex);
				if (wallDir != null) {
					Vec3 intoWallVec = new Vec3(-wallDir.getStepX(), 0, -wallDir.getStepZ());
					float targetWorldYaw = (float) Math.toDegrees(Math.atan2(-intoWallVec.x, intoWallVec.z)) + 180f;
					float yawToWall = Mth.wrapDegrees(targetWorldYaw - this.player.yBodyRot);
					float absYaw = Math.abs(yawToWall);

					// 【核心保留】：动画加载第一帧直接锁定目标姿势
					if (this.isFirstTick) {
						this.isFirstTick = false;
						if (absYaw > 130f) {
							this.isBackPose = true;
							this.currentBackWeight = 1.0f;
							this.prevBackWeight = 1.0f;
						} else {
							this.isBackPose = false;
							this.currentBackWeight = 0.0f;
							this.prevBackWeight = 0.0f;
						}
					} else {
						// 正常迟滞判定
						if (!this.isBackPose && absYaw > 130f) {
							this.isBackPose = true;
						} else if (this.isBackPose && absYaw < 110f) {
							this.isBackPose = false;
						}
					}
				}
			}

			float targetWeight = this.isBackPose ? 1.0f : 0.0f;
			float transitionSpeed = 0.25f;

			if (this.currentBackWeight < targetWeight) {
				this.currentBackWeight = Math.min(1.0f, this.currentBackWeight + transitionSpeed);
			} else if (this.currentBackWeight > targetWeight) {
				this.currentBackWeight = Math.max(0.0f, this.currentBackWeight - transitionSpeed);
			}
		}
	}

	@Override
	public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
		bone = super.get3DTransform(bone);
		String boneName = bone.getName();

		float bodyYaw = Mth.lerp(this.partialTick, this.player.yBodyRotO, this.player.yBodyRot);

		if (boneName.equals("head")) {
			float pitch = Mth.lerp(this.partialTick, this.player.xRotO, this.player.getXRot());
			float headYaw = Mth.lerp(this.partialTick, this.player.yHeadRotO, this.player.getYHeadRot());
			float netYaw = Mth.wrapDegrees(headYaw - bodyYaw);

			bone.rotX = pitch * Mth.DEG_TO_RAD;
			bone.rotY = netYaw * Mth.DEG_TO_RAD;
			return bone;
		}

		WallData wallData = ParkourContext.get(this.player).wallData();
		int dirIndex = wallData.getWallSlideDirection();
		if (dirIndex < 0 || dirIndex > 5) return bone;
		Direction wallDir = Direction.from3DDataValue(dirIndex);
		if (wallDir == null) return bone;

		Vec3 intoWallVec = new Vec3(-wallDir.getStepX(), 0, -wallDir.getStepZ());
		float targetWorldYaw = (float) Math.toDegrees(Math.atan2(-intoWallVec.x, intoWallVec.z)) + 180f;
		float yawToWall = Mth.wrapDegrees(targetWorldYaw - bodyYaw);

		boolean isWallOnRight = yawToWall > 0;
		float backWeight = Mth.lerp(this.partialTick, this.prevBackWeight, this.currentBackWeight);

		// ==========================================
		// 【回退】：最稳定、干净的经典手部追踪姿势
		// ==========================================

		// --- 姿态 1: 侧身/面朝墙 (单手高举，安全角度内追踪) ---
		float frontLocalYaw = Mth.clamp(yawToWall * 0.3f, -35f, 35f) * Mth.DEG_TO_RAD;
		float FRONT_PITCH = (float) Math.toRadians(-155);
		float FRONT_LEAN = (float) Math.toRadians(12);

		float frX = isWallOnRight ? FRONT_PITCH : 0.2f;
		float frY = isWallOnRight ? frontLocalYaw : 0f;
		float frZ = isWallOnRight ? FRONT_LEAN : 0.2f;

		float flX = !isWallOnRight ? FRONT_PITCH : 0.2f;
		float flY = !isWallOnRight ? frontLocalYaw : 0f;
		float flZ = !isWallOnRight ? -FRONT_LEAN : -0.2f;

		// --- 姿态 2: 背对墙 (双手向后方墙壁支撑) ---
		float BACK_PITCH = 0.5f;
		float BACK_YAW = 0.3f;
		float BACK_ROLL = 0.2f;

		float brX = BACK_PITCH, brY = BACK_YAW, brZ = BACK_ROLL;
		float blX = BACK_PITCH, blY = -BACK_YAW, blZ = -BACK_ROLL;

		// --- 丝滑应用插值 ---
		switch (boneName) {
			case "right_arm":
				bone.rotX = Mth.lerp(backWeight, frX, brX);
				bone.rotY = Mth.lerp(backWeight, frY, brY);
				bone.rotZ = Mth.lerp(backWeight, frZ, brZ);
				break;

			case "left_arm":
				bone.rotX = Mth.lerp(backWeight, flX, blX);
				bone.rotY = Mth.lerp(backWeight, flY, blY);
				bone.rotZ = Mth.lerp(backWeight, flZ, blZ);
				break;

			case "body":
				// 仅保留轻微的物理摩擦震动感
				float smoothTick = this.player.tickCount + this.partialTick;
				bone.positionY += Mth.sin(smoothTick * 1.5f) * 0.2f;
				break;
		}

		return bone;
	}
}