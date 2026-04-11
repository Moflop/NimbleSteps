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
 * 地面移动数据上下文
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroundData {
	// 爬行
	private boolean crawling = false;
	// 滑铲
	private int slideCooldown = 0;
	// 落地翻滚
	private int landingRollWindow = 0;
	private int landingRollDuration = 0;

	public static final Codec<GroundData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			// 序列化的键名保持 "isCrawling" 不变，以兼容老数据
			Codec.BOOL.optionalFieldOf("isCrawling", false).forGetter(GroundData::isCrawling),
			Codec.INT.optionalFieldOf("slideCooldown", 0).forGetter(GroundData::getSlideCooldown),
			Codec.INT.optionalFieldOf("landingRollWindow", 0).forGetter(GroundData::getLandingRollWindow),
			Codec.INT.optionalFieldOf("landingRollDuration", 0).forGetter(GroundData::getLandingRollDuration)
		).apply(instance, GroundData::new) // 大幅简化
	);

	public static final StreamCodec<ByteBuf, GroundData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, GroundData::isCrawling,
		ByteBufCodecs.VAR_INT, GroundData::getSlideCooldown,
		ByteBufCodecs.VAR_INT, GroundData::getLandingRollWindow,
		ByteBufCodecs.VAR_INT, GroundData::getLandingRollDuration,
		GroundData::new // 大幅简化
	);

	public void copyFrom(GroundData other) {
		this.crawling = other.crawling;
		this.slideCooldown = other.slideCooldown;
		this.landingRollWindow = other.landingRollWindow;
		this.landingRollDuration = other.landingRollDuration;
	}
}