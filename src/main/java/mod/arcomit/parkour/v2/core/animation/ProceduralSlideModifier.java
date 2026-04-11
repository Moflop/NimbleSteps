package mod.arcomit.parkour.v2.core.animation;

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
		// 1. 获取 Blockbench 里 K 好的基础姿势 (包含了 X-70, Y5, Z9 的复杂扭曲)
		bone = super.get3DTransform(bone);

		if (bone.getName().equals("head")) {
			// 2. 获取鼠标左右偏移量 (Yaw)
			float headYaw = Mth.lerp(this.partialTick, this.player.yHeadRotO, this.player.getYHeadRot());
			float bodyYaw = Mth.lerp(this.partialTick, this.player.yBodyRotO, this.player.yBodyRot);
			float netYaw = headYaw - bodyYaw;

			// 转化为弧度。如果进游戏发现鼠标往左看，头却往右转，请在这里加上负号：-netYaw
			float yawRad = netYaw * ((float) Math.PI / 180f);

			// 3. 使用标准 JOML 将当前骨骼角度 (弧度) 转为四元数
			// 注意：Blockbench 和 Minecraft 底层通常遵循 Z-Y-X 的顺规
			//Quaternionf q = new Quaternionf().rotationZYX(bone.rotZ, bone.rotY, bone.rotX);
			Quaternionf q = new Quaternionf().rotationZYX(bone.rotX, bone.rotY, bone.rotZ);

			// 4. 【核心魔法】绕着头部当前的【局部 Y 轴】(即倾斜状态下的脖子) 旋转鼠标角度！
			q.rotateLocalY(yawRad);

			// 5. 使用原生标准方法提欧拉角
			Vector3f dest = new Vector3f();
			q.getEulerAnglesZYX(dest); // 提取出来的依然是弧度

			// 6. 重新赋值给骨骼。JOML 的 getEulerAnglesZYX 存入 Vector3f 的顺序是：x=Z, y=Y, z=X
			bone.rotZ = dest.x;
			bone.rotY = dest.y;
			bone.rotX = dest.z;
		} else if (bone.getName().equals("body")) {
			// 如果你在 Blockbench 里用来控制全身平移的骨骼叫 "torso" 或 "action_root"，请把上面的 "body" 换掉

			// 1. 获取绝对平滑的系统时间 (Tick + 帧补偿)
			float smoothTick = this.player.tickCount + this.partialTick;

			// 2. 将 Tick 转换为秒 (Minecraft 1秒 = 20 Tick)
			float timeInSeconds = smoothTick / 20.0f;

			// 3. 计算角频率 (Omega)。公式是：2 * PI / 周期时间
			// 你的要求是 0.28 秒一个完整循环
			float omega = (float) (2 * Math.PI / 0.28f);

			// 4. 设置晃动幅度 (像素格)
			// 1.5f 意思是向左偏 1.5，向右偏 1.5。如果你觉得晃得太轻/太重，就改这个值！
			float amplitude = 0.2f;

			// 5. 核心数学：计算当前时间的实时偏移量
			float shift = Mth.sin(timeInSeconds * omega) * amplitude;

			// 6. 应用到骨骼的局部 X 轴 (也就是 Blockbench 里的左右平移)
			// 如果你的身体已经转了 -90 度导致 X 变成了上下，请把这里的 positionX 换成 positionZ 试试！
			bone.positionX += shift;
		}
		return bone;
	}
}