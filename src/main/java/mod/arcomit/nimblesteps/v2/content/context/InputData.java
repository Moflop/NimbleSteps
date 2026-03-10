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
 * 输入数据上下文
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class InputData {
	private float leftImpulse = 0f;
	private boolean upKeyPressed = false;

	public static final Codec<InputData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.FLOAT.optionalFieldOf("leftImpulse", 0f).forGetter(InputData::getLeftImpulse),
			Codec.BOOL.optionalFieldOf("upKeyPressed", false).forGetter(InputData::isUpKeyPressed)
		).apply(instance, InputData::new)
	);

	public static final StreamCodec<ByteBuf, InputData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.FLOAT, InputData::getLeftImpulse,
		ByteBufCodecs.BOOL, InputData::isUpKeyPressed,
		InputData::new
	);

	public void copyFrom(InputData other) {
		this.leftImpulse = other.leftImpulse;
		this.upKeyPressed = other.upKeyPressed;
	}
}