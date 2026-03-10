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
 * 游泳移动数据上下文
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwimMovementData {
	private int swimmingBoostCooldown = 0;

	public static final Codec<SwimMovementData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.INT.optionalFieldOf("swimmingBoostCooldown", 0).forGetter(SwimMovementData::getSwimmingBoostCooldown)
		).apply(instance, SwimMovementData::new)
	);

	public static final StreamCodec<ByteBuf, SwimMovementData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, SwimMovementData::getSwimmingBoostCooldown,
		SwimMovementData::new
	);

	public void copyFrom(SwimMovementData other) {
		this.swimmingBoostCooldown = other.swimmingBoostCooldown;
	}
}