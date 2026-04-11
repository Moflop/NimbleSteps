package mod.arcomit.parkour.v2.core.animation;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ProceduralCrawlModifier extends AbstractModifier {

	private final Player player;
	// 用于记录当前渲染帧的插值进度
	private float partialTick;

	public ProceduralCrawlModifier(Player player) {
		this.player = player;
	}

	@Override
	public void setupAnim(AnimationData state) {
		super.setupAnim(state);
		this.partialTick = state.getPartialTick();
	}

	@Override
	public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
		// 先获取你在 Blockbench 里 K 好的底层爬行姿势
		bone = super.get3DTransform(bone);

		// 【核心魔法】直接读取 Minecraft 原版引擎计算好的、完美插值的肢体摆动数据！
		// speed: 玩家当前的移动幅度 (静止为0，全速移动为1)
		float swingAmount = this.player.walkAnimation.speed(this.partialTick);
		// position: 玩家移动的相位进度 (天然的正弦波周期)
		float swingProgress = this.player.walkAnimation.position(this.partialTick);

		// 如果玩家正在移动 (容差值)
		if (swingAmount > 0.01f) {
			// 直接计算平滑的摆动角度
			// 乘数 0.8f 和 0.6f 分别控制爬行时四肢摆动的“频率”和“幅度”，你可以根据手感自由微调
			float sinWave = Mth.sin(swingProgress * 0.8f) * swingAmount * 0.6f;

			switch (bone.getName()) {
				case "left_arm":
					bone.rotX += sinWave;
					break;
				case "right_arm":
					bone.rotX -= sinWave;
					break;
				case "left_leg":
					bone.rotX -= sinWave;
					break;
				case "right_leg":
					bone.rotX += sinWave;
					break;
			}
		}

		return bone;
	}
}