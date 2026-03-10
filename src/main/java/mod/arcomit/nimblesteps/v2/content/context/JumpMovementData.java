package mod.arcomit.nimblesteps.v2.content.context;

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
public class JumpMovementData {
	// 去掉 has 前缀
	private boolean jumped = false;
	private int lastWallJumpDirection = -1;
	private int ticksSinceLastJump = 100;

	public void resetLastWallJumpDirection() {
		this.lastWallJumpDirection = -1;
	}

	public static final Codec<JumpMovementData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			// 序列化的键名保持 "hasJumped" 不变
			Codec.BOOL.optionalFieldOf("hasJumped", false).forGetter(JumpMovementData::isJumped),
			Codec.INT.optionalFieldOf("lastWallJumpDirection", -1).forGetter(JumpMovementData::getLastWallJumpDirection),
			Codec.INT.optionalFieldOf("ticksSinceLastJump", 100).forGetter(JumpMovementData::getTicksSinceLastJump)
		).apply(instance, JumpMovementData::new)
	);

	public static final StreamCodec<ByteBuf, JumpMovementData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, JumpMovementData::isJumped,
		ByteBufCodecs.VAR_INT, JumpMovementData::getLastWallJumpDirection,
		ByteBufCodecs.VAR_INT, JumpMovementData::getTicksSinceLastJump,
		JumpMovementData::new
	);

	public void copyFrom(JumpMovementData other) {
		this.jumped = other.jumped;
		this.lastWallJumpDirection = other.lastWallJumpDirection;
		this.ticksSinceLastJump = other.ticksSinceLastJump;
	}
}