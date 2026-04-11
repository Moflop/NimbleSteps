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
 * 墙体移动数据上下文
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class WallData {
	private int wallRunDuration = 0;
	private int wallRunCount = 0;

	private boolean wallSliding = false;
	private int wallSlideJumpReleaseGraceTicks = 0;
	private int wallSlideDirection = -1;

	private boolean armHanging = false;
	private int armHangingDirection = -1;

	private int wallClimbDuration = 0;

	private boolean wallClimbed = false;

	private int mountDuration = 0;
	private double obstaclesHeight = 0.0;

	public boolean isWallRunning() { return wallRunDuration > 0; }
	public boolean isWallClimbing() { return wallClimbDuration > 0; }
	public boolean isMounting() { return mountDuration > 0; }

	public void resetArmHangingDirection() {
		this.armHangingDirection = -1;
	}

	public static final Codec<WallData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.INT.optionalFieldOf("wallRunDuration", 0).forGetter(WallData::getWallRunDuration),
			Codec.INT.optionalFieldOf("wallRunCount", 0).forGetter(WallData::getWallRunCount),
			Codec.BOOL.optionalFieldOf("isWallSliding", false).forGetter(WallData::isWallSliding),
			Codec.INT.optionalFieldOf("wallSlideJumpReleaseGraceTicks", 0).forGetter(WallData::getWallSlideJumpReleaseGraceTicks),
			Codec.INT.optionalFieldOf("wallSlideDirection", -1).forGetter(WallData::getWallSlideDirection),
			Codec.BOOL.optionalFieldOf("isArmHanging", false).forGetter(WallData::isArmHanging),
			Codec.INT.optionalFieldOf("armHangingDirection", -1).forGetter(WallData::getArmHangingDirection),
			Codec.INT.optionalFieldOf("wallClimbDuration", 0).forGetter(WallData::getWallClimbDuration),
			Codec.BOOL.optionalFieldOf("hasWallClimbed", false).forGetter(WallData::isWallClimbed),
			Codec.INT.optionalFieldOf("mountDuration", 0).forGetter(WallData::getMountDuration),
			Codec.DOUBLE.optionalFieldOf("obstaclesHeight", 0.0).forGetter(WallData::getObstaclesHeight)
		).apply(instance, WallData::new)
	);

	public static final StreamCodec<ByteBuf, WallData> STREAM_CODEC = StreamCodec.of(
		(buf, data) -> {
			ByteBufCodecs.VAR_INT.encode(buf, data.getWallRunDuration());
			ByteBufCodecs.VAR_INT.encode(buf, data.getWallRunCount());
			ByteBufCodecs.BOOL.encode(buf, data.isWallSliding());
			ByteBufCodecs.VAR_INT.encode(buf, data.getWallSlideJumpReleaseGraceTicks());
			ByteBufCodecs.VAR_INT.encode(buf, data.getWallSlideDirection());
			ByteBufCodecs.BOOL.encode(buf, data.isArmHanging());
			ByteBufCodecs.VAR_INT.encode(buf, data.getArmHangingDirection());
			ByteBufCodecs.VAR_INT.encode(buf, data.getWallClimbDuration());
			ByteBufCodecs.BOOL.encode(buf, data.isWallClimbed());
			ByteBufCodecs.VAR_INT.encode(buf, data.getMountDuration());
			ByteBufCodecs.DOUBLE.encode(buf, data.getObstaclesHeight());
		},
		buf -> new WallData(
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.BOOL.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.BOOL.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.BOOL.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.DOUBLE.decode(buf)
		)
	);

	public void copyFrom(WallData other) {
		this.wallRunDuration = other.wallRunDuration;
		this.wallRunCount = other.wallRunCount;
		this.wallSliding = other.wallSliding;
		this.wallSlideJumpReleaseGraceTicks = other.wallSlideJumpReleaseGraceTicks;
		this.wallSlideDirection = other.wallSlideDirection;
		this.armHanging = other.armHanging;
		this.armHangingDirection = other.armHangingDirection;
		this.wallClimbDuration = other.wallClimbDuration;
		this.wallClimbed = other.wallClimbed;
		this.mountDuration = other.mountDuration;
		this.obstaclesHeight = other.obstaclesHeight;
	}
}