package mod.arcomit.parkour.core.context;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.core.Direction;
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
public class JumpData {
	// 去掉 has 前缀
	private boolean jumped = false;

	private int lastViewWallJumpDir3DData = -1;
	private int lastUpWallJumpDir3DData = -1;
	private int lastParallelWallJumpDir3DData = -1;

	private transient Direction lastViewWallJumpDir;
	private transient Direction lastUpWallJumpDir;
	private transient Direction lastParallelWallJumpDir;

	private int ticksSinceLastJump = 100;

	private int jumpReleaseGraceTicks = 0;

	public static final Codec<JumpData> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			// 序列化的键名保持 "hasJumped" 不变
			Codec.BOOL.optionalFieldOf("isJumped", false).forGetter(JumpData::isJumped),
			Codec.INT.optionalFieldOf("lastLastViewWallJumpDir3DData", -1).forGetter(JumpData::getLastViewWallJumpDir3DData),
			Codec.INT.optionalFieldOf("lastUpWallJumpDir3DData", -1).forGetter(JumpData::getLastUpWallJumpDir3DData),
			Codec.INT.optionalFieldOf("lastParallelWallJumpDir3DData", -1).forGetter(JumpData::getLastParallelWallJumpDir3DData),
			Codec.INT.optionalFieldOf("ticksSinceLastJump", 100).forGetter(JumpData::getTicksSinceLastJump),
			Codec.INT.optionalFieldOf("jumpReleaseGraceTicks", 0).forGetter(JumpData::getJumpReleaseGraceTicks)
		).apply(instance, JumpData::new)
	);

	public static final StreamCodec<ByteBuf, JumpData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, JumpData::isJumped,
		ByteBufCodecs.VAR_INT, JumpData::getLastViewWallJumpDir3DData,
		ByteBufCodecs.VAR_INT, JumpData::getLastUpWallJumpDir3DData,
		ByteBufCodecs.VAR_INT, JumpData::getLastParallelWallJumpDir3DData,
		ByteBufCodecs.VAR_INT, JumpData::getTicksSinceLastJump,
		ByteBufCodecs.VAR_INT, JumpData::getJumpReleaseGraceTicks,
		JumpData::new
	);

	public JumpData(boolean jumped, int lastViewWallJumpDir3DData,
	                int lastUpWallJumpDir3DData, int lastParallelWallJumpDir3DData,
	                int ticksSinceLastJump, int jumpReleaseGraceTicks) {
		this.jumped = jumped;
		this.lastViewWallJumpDir3DData = lastViewWallJumpDir3DData;
		this.lastUpWallJumpDir3DData = lastUpWallJumpDir3DData;
		this.lastParallelWallJumpDir3DData = lastParallelWallJumpDir3DData;
		this.ticksSinceLastJump = ticksSinceLastJump;
		this.jumpReleaseGraceTicks = jumpReleaseGraceTicks;
	}

	public void copyFrom(JumpData other) {
		this.jumped = other.jumped;
		this.lastViewWallJumpDir3DData = other.lastViewWallJumpDir3DData;
		this.lastUpWallJumpDir3DData = other.lastUpWallJumpDir3DData;
		this.lastParallelWallJumpDir3DData = other.lastParallelWallJumpDir3DData;
		this.ticksSinceLastJump = other.ticksSinceLastJump;
		this.jumpReleaseGraceTicks = other.jumpReleaseGraceTicks;
	}

	public Direction getLastViewWallJumpDir() {
		if (lastViewWallJumpDir == null) {
			if (lastViewWallJumpDir3DData == -1) {
				return null;
			}
			lastViewWallJumpDir = Direction.from3DDataValue(lastViewWallJumpDir3DData);
		}
		return lastViewWallJumpDir;
	}
	public void setLastViewWallJumpDir(Direction dir) {
		lastViewWallJumpDir = dir;
		lastViewWallJumpDir3DData = dir == null ? -1 : dir.get3DDataValue();
	}
	public void resetLastViewWallJumpDir() {
		lastViewWallJumpDir = null;
		lastViewWallJumpDir3DData = -1;
	}

	public Direction getLastUpWallJumpDir() {
		if (lastUpWallJumpDir == null) {
			if (lastUpWallJumpDir3DData == -1) {
				return null;
			}
			lastUpWallJumpDir = Direction.from3DDataValue(lastUpWallJumpDir3DData);
		}
		return lastUpWallJumpDir;
	}
	public void setLastUpWallJumpDir(Direction dir) {
		lastUpWallJumpDir = dir;
		lastUpWallJumpDir3DData = dir == null ? -1 : dir.get3DDataValue();
	}
	public void resetLastUpWallJumpDir() {
		lastUpWallJumpDir = null;
		lastUpWallJumpDir3DData = -1;
	}

	public Direction getLastParallelWallJumpDir() {
		if (lastParallelWallJumpDir == null) {
			if (lastParallelWallJumpDir3DData == -1) {
				return null;
			}
			lastParallelWallJumpDir = Direction.from3DDataValue(lastParallelWallJumpDir3DData);
		}
		return lastParallelWallJumpDir;
	}
	public void setLastParallelWallJumpDir(Direction dir) {
		lastParallelWallJumpDir = dir;
		lastParallelWallJumpDir3DData = dir == null ? -1 : dir.get3DDataValue();
	}
	public void resetLastParallelWallJumpDir() {
		lastParallelWallJumpDir = null;
		lastParallelWallJumpDir3DData = -1;
	}
}