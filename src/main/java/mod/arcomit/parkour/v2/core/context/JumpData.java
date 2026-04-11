package mod.arcomit.parkour.v2.core.context;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * 跳跃移动数据上下文
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class JumpData {
	// 去掉 has 前缀
	private boolean jumped = false;
	private int lastWallJumpDirection = -1;
	private int ticksSinceLastJump = 100;

	public void resetLastWallJumpDirection() {
		this.lastWallJumpDirection = -1;
	}

	public static final Codec<JumpData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			// 序列化的键名保持 "hasJumped" 不变
			Codec.BOOL.optionalFieldOf("hasJumped", false).forGetter(JumpData::isJumped),
			Codec.INT.optionalFieldOf("lastWallJumpDirection", -1).forGetter(JumpData::getLastWallJumpDirection),
			Codec.INT.optionalFieldOf("ticksSinceLastJump", 100).forGetter(JumpData::getTicksSinceLastJump)
		).apply(instance, JumpData::new)
	);

	public static final StreamCodec<ByteBuf, JumpData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, JumpData::isJumped,
		ByteBufCodecs.VAR_INT, JumpData::getLastWallJumpDirection,
		ByteBufCodecs.VAR_INT, JumpData::getTicksSinceLastJump,
		JumpData::new
	);

	public void copyFrom(JumpData other) {
		this.jumped = other.jumped;
		this.lastWallJumpDirection = other.lastWallJumpDirection;
		this.ticksSinceLastJump = other.ticksSinceLastJump;
	}
}