package mod.arcomit.parkour.v2.content.client.animation.player.modifier;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ProceduralSlideModifier extends AbstractModifier {

	private final Player player;
	private float partialTick = 0f;

	public ProceduralSlideModifier(Player player) {
		this.player = player;
	}

	@Override
	public void setupAnim(AnimationData state) {
		super.setupAnim(state);
		this.partialTick = state.getPartialTick();
	}

	@Override
	public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
		bone = super.get3DTransform(bone);
		String boneName = bone.getName();

		if (boneName.equals("head")) {
			// 滑铲时头部朝向依旧受鼠标控制
			float headYaw = Mth.lerp(this.partialTick, this.player.yHeadRotO, this.player.getYHeadRot());
			float bodyYaw = Mth.lerp(this.partialTick, this.player.yBodyRotO, this.player.yBodyRot);
			float netYaw = headYaw - bodyYaw;

			float yawRad = netYaw * ((float) Math.PI / 180f);

			Quaternionf q = new Quaternionf().rotationZYX(bone.rotX, bone.rotY, bone.rotZ);

			q.rotateLocalY(yawRad);

			Vector3f dest = new Vector3f();
			q.getEulerAnglesZYX(dest);

			bone.rotZ = dest.x;
			bone.rotY = dest.y;
			bone.rotX = dest.z;
		} else if (boneName.equals("body")) {
			// 滑铲时身体轻微摇晃
			float smoothTick = this.player.tickCount + this.partialTick;

			float timeInSeconds = smoothTick / 20.0f;

			// 计算角频率 (Omega)。公式是：2 * PI / 周期时间
			// 0.28 秒一个完整循环
			float omega = (float) (2 * Math.PI / 0.28f);

			// 晃动幅度 (像素格)
			// 1.5f 意思是向左偏 1.5，向右偏 1.5。
			float amplitude = 0.2f;

			float shift = Mth.sin(timeInSeconds * omega) * amplitude;
			bone.positionX += shift;
		}
		return bone;
	}
}