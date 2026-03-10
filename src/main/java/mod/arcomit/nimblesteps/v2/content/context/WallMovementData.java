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
 * 墙体移动数据上下文
 *
 * @author Arcomit
 * @since 2026-03-10
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class WallMovementData {
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

	public static final Codec<WallMovementData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.INT.optionalFieldOf("wallRunDuration", 0).forGetter(WallMovementData::getWallRunDuration),
			Codec.INT.optionalFieldOf("wallRunCount", 0).forGetter(WallMovementData::getWallRunCount),
			Codec.BOOL.optionalFieldOf("isWallSliding", false).forGetter(WallMovementData::isWallSliding),
			Codec.INT.optionalFieldOf("wallSlideJumpReleaseGraceTicks", 0).forGetter(WallMovementData::getWallSlideJumpReleaseGraceTicks),
			Codec.INT.optionalFieldOf("wallSlideDirection", -1).forGetter(WallMovementData::getWallSlideDirection),
			Codec.BOOL.optionalFieldOf("isArmHanging", false).forGetter(WallMovementData::isArmHanging),
			Codec.INT.optionalFieldOf("armHangingDirection", -1).forGetter(WallMovementData::getArmHangingDirection),
			Codec.INT.optionalFieldOf("wallClimbDuration", 0).forGetter(WallMovementData::getWallClimbDuration),
			Codec.BOOL.optionalFieldOf("hasWallClimbed", false).forGetter(WallMovementData::isWallClimbed),
			Codec.INT.optionalFieldOf("mountDuration", 0).forGetter(WallMovementData::getMountDuration),
			Codec.DOUBLE.optionalFieldOf("obstaclesHeight", 0.0).forGetter(WallMovementData::getObstaclesHeight)
		).apply(instance, WallMovementData::new)
	);

	public static final StreamCodec<ByteBuf, WallMovementData> STREAM_CODEC = StreamCodec.of(
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
		buf -> new WallMovementData(
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

	public void copyFrom(WallMovementData other) {
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