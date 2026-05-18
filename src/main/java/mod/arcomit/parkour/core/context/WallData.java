package mod.arcomit.parkour.core.context;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.core.Direction;
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
	private int wallClimbCollisionDir3DData = -1;
	private int wallSlideCollisionDir3DData = -1;

	private boolean armHanging = false;
	private int armHangingDir = -1;

	private int mountDuration3DData = 0;
	private double obstaclesHeight = 0.0;
	
	private Direction wallRunCollisionDir;
	private Direction wallRunMovementDir;
	private Direction wallClimbCollisionDir;
	private Direction wallSlideCollisionDir;

	public static final Codec<WallData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.INT.optionalFieldOf("wallRunCollisionDir3DData", 0).forGetter(WallData::getWallRunCollisionDir3DData),
			Codec.INT.optionalFieldOf("wallRunMovementDir3DData", 0).forGetter(WallData::getWallRunMovementDir3DData),
			Codec.INT.optionalFieldOf("wallClimbCollisionDir3DData", 0).forGetter(WallData::getWallClimbCollisionDir3DData),
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
			ByteBufCodecs.VAR_INT.encode(buf, data.getWallClimbCollisionDir3DData());
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
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.BOOL.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.DOUBLE.decode(buf)
		)
	);

	public WallData(int wallRunCollisionDir3DData, int wallRunMovementDir3DData,
			int wallClimbCollisionDir3DData, int wallSlideCollisionDir3DData,
			boolean armHanging, int armHangingDir,
			int mountDuration3DData, double obstaclesHeight) {
		this.wallRunCollisionDir3DData = wallRunCollisionDir3DData;
		this.wallRunMovementDir3DData = wallRunMovementDir3DData;
		this.wallClimbCollisionDir3DData = wallClimbCollisionDir3DData;
		this.wallSlideCollisionDir3DData = wallSlideCollisionDir3DData;
		this.armHanging = armHanging;
		this.armHangingDir = armHangingDir;
		this.mountDuration3DData = mountDuration3DData;
		this.obstaclesHeight = obstaclesHeight;
	}

	public void copyFrom(WallData other) {
		this.wallRunCollisionDir3DData = other.wallRunCollisionDir3DData;
		this.wallRunMovementDir3DData = other.wallRunMovementDir3DData;
		this.wallClimbCollisionDir3DData = other.wallClimbCollisionDir3DData;
		this.wallSlideCollisionDir3DData = other.wallSlideCollisionDir3DData;
		this.armHanging = other.armHanging;
		this.armHangingDir = other.armHangingDir;
		this.mountDuration3DData = other.mountDuration3DData;
		this.obstaclesHeight = other.obstaclesHeight;
	}

	public Direction getWallRunCollisionDir() {
		if (wallRunCollisionDir == null) {
			if (wallRunCollisionDir3DData == -1) {
				return null;
			}
			wallRunCollisionDir = Direction.from3DDataValue(wallRunCollisionDir3DData);
		}
		return wallRunCollisionDir;
	}
	public void setWallRunCollisionDir(Direction dir) {
		wallRunCollisionDir = dir;
		wallRunCollisionDir3DData = dir == null ? -1 : dir.get3DDataValue();
	}
	public void resetWallRunCollisionDir() {
		wallRunCollisionDir = null;
		wallRunCollisionDir3DData = -1;
	}

	public Direction getWallRunMovementDir() {
		if (wallRunMovementDir == null) {
			if (wallRunMovementDir3DData == -1) {
				return null;
			}
			wallRunMovementDir = Direction.from3DDataValue(wallRunMovementDir3DData);
		}
		return wallRunMovementDir;
	}
	public void setWallRunMovementDir(Direction dir) {
		wallRunMovementDir = dir;
		wallRunMovementDir3DData = dir == null ? -1 : dir.get3DDataValue();
	}
	public void resetWallRunMovementDir() {
		wallRunMovementDir = null;
		wallRunMovementDir3DData = -1;
	}

	public Direction getWallClimbCollisionDir() {
		if (wallClimbCollisionDir == null) {
			if (wallClimbCollisionDir3DData == -1) {
				return null;
			}
			wallClimbCollisionDir = Direction.from3DDataValue(wallClimbCollisionDir3DData);
		}
		return wallClimbCollisionDir;
	}
	public void setWallClimbCollisionDir(Direction dir) {
		wallClimbCollisionDir = dir;
		wallClimbCollisionDir3DData = dir == null ? -1 : dir.get3DDataValue();
	}
	public void resetWallClimbCollisionDir() {
		wallClimbCollisionDir = null;
		wallClimbCollisionDir3DData = -1;
	}

	public Direction getWallSlideCollisionDir() {
		if (wallSlideCollisionDir == null) {
			if (wallSlideCollisionDir3DData == -1) {
				return null;
			}
			wallSlideCollisionDir = Direction.from3DDataValue(wallSlideCollisionDir3DData);
		}
		return wallSlideCollisionDir;
	}
	public void setWallSlideCollisionDir(Direction dir) {
		wallSlideCollisionDir = dir;
		wallSlideCollisionDir3DData = dir == null ? -1 : dir.get3DDataValue();
	}
	public void resetWallSlideCollisionDir() {
		wallSlideCollisionDir = null;
		wallSlideCollisionDir3DData = -1;
	}
}