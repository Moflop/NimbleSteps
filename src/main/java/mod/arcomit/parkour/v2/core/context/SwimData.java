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
 * 游泳移动数据上下文
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwimData {
	private int swimmingBoostCooldown = 0;

	public static final Codec<SwimData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.INT.optionalFieldOf("swimmingBoostCooldown", 0).forGetter(SwimData::getSwimmingBoostCooldown)
		).apply(instance, SwimData::new)
	);

	public static final StreamCodec<ByteBuf, SwimData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, SwimData::getSwimmingBoostCooldown,
		SwimData::new
	);

	public void copyFrom(SwimData other) {
		this.swimmingBoostCooldown = other.swimmingBoostCooldown;
	}
}