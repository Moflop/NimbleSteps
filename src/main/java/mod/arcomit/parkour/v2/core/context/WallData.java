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
	private int wallRunCollisionDir3DData = -1;
	private int wallRunMovementDir3DData = -1;

	private int wallSlideCollisionDir3DData = -1;

	private boolean armHanging = false;
	private int armHangingDir = -1;

	private int mountDuration3DData = 0;
	private double obstaclesHeight = 0.0;

	public boolean isMounting() { return mountDuration3DData > 0; }

	public void resetArmHangingDirection() {
		this.armHangingDir = -1;
	}

	public static final Codec<WallData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.INT.optionalFieldOf("wallRunCollisionDir3DData", 0).forGetter(WallData::getWallRunCollisionDir3DData),
			Codec.INT.optionalFieldOf("wallRunMovementDir3DData", 0).forGetter(WallData::getWallRunMovementDir3DData),
			Codec.INT.optionalFieldOf("wallSlideCollisionDir3DData", -1).forGetter(WallData::getWallSlideCollisionDir3DData),
			Codec.BOOL.optionalFieldOf("isArmHanging", false).forGetter(WallData::isArmHanging),
			Codec.INT.optionalFieldOf("armHangingDir3DData", -1).forGetter(WallData::getArmHangingDir),
			Codec.INT.optionalFieldOf("mountDuration", 0).forGetter(WallData::getMountDuration3DData),
			Codec.DOUBLE.optionalFieldOf("obstaclesHeight", 0.0).forGetter(WallData::getObstaclesHeight)
		).apply(instance, WallData::new)
	);

	public static final StreamCodec<ByteBuf, WallData> STREAM_CODEC = StreamCodec.of(
		(buf, data) -> {
			ByteBufCodecs.VAR_INT.encode(buf, data.getWallRunCollisionDir3DData());
			ByteBufCodecs.VAR_INT.encode(buf, data.getWallRunMovementDir3DData());
			ByteBufCodecs.VAR_INT.encode(buf, data.getWallSlideCollisionDir3DData());
			ByteBufCodecs.BOOL.encode(buf, data.isArmHanging());
			ByteBufCodecs.VAR_INT.encode(buf, data.getArmHangingDir());
			ByteBufCodecs.VAR_INT.encode(buf, data.getMountDuration3DData());
			ByteBufCodecs.DOUBLE.encode(buf, data.getObstaclesHeight());
		},
		buf -> new WallData(
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.BOOL.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.DOUBLE.decode(buf)
		)
	);

	public void copyFrom(WallData other) {
		this.wallRunCollisionDir3DData = other.wallRunCollisionDir3DData;
		this.wallRunMovementDir3DData = other.wallRunMovementDir3DData;
		this.wallSlideCollisionDir3DData = other.wallSlideCollisionDir3DData;
		this.armHanging = other.armHanging;
		this.armHangingDir = other.armHangingDir;
		this.mountDuration3DData = other.mountDuration3DData;
		this.obstaclesHeight = other.obstaclesHeight;
	}
}