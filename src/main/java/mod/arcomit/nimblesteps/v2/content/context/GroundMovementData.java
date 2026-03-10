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
 * 地面移动数据上下文
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroundMovementData {
	// 爬行 (去掉 is 前缀)
	private boolean crawling = false;
	// 滑铲
	private int slideCooldown = 0;
	private int slideDuration = 0;
	// 落地翻滚
	private int landingRollWindow = 0;
	private int landingRollDuration = 0;

	public boolean isSliding() {
		return slideDuration > 0;
	}

	public static final Codec<GroundMovementData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			// 序列化的键名保持 "isCrawling" 不变，以兼容老数据
			Codec.BOOL.optionalFieldOf("isCrawling", false).forGetter(GroundMovementData::isCrawling),
			Codec.INT.optionalFieldOf("slideCooldown", 0).forGetter(GroundMovementData::getSlideCooldown),
			Codec.INT.optionalFieldOf("slideDuration", 0).forGetter(GroundMovementData::getSlideDuration),
			Codec.INT.optionalFieldOf("landingRollWindow", 0).forGetter(GroundMovementData::getLandingRollWindow),
			Codec.INT.optionalFieldOf("landingRollDuration", 0).forGetter(GroundMovementData::getLandingRollDuration)
		).apply(instance, GroundMovementData::new) // 大幅简化
	);

	public static final StreamCodec<ByteBuf, GroundMovementData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, GroundMovementData::isCrawling,
		ByteBufCodecs.VAR_INT, GroundMovementData::getSlideCooldown,
		ByteBufCodecs.VAR_INT, GroundMovementData::getSlideDuration,
		ByteBufCodecs.VAR_INT, GroundMovementData::getLandingRollWindow,
		ByteBufCodecs.VAR_INT, GroundMovementData::getLandingRollDuration,
		GroundMovementData::new // 大幅简化
	);

	public void copyFrom(GroundMovementData other) {
		this.crawling = other.crawling;
		this.slideCooldown = other.slideCooldown;
		this.slideDuration = other.slideDuration;
		this.landingRollWindow = other.landingRollWindow;
		this.landingRollDuration = other.landingRollDuration;
	}
}