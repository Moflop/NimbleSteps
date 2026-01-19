package mod.arcomit.nimblesteps.attachment;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import mod.arcomit.nimblesteps.init.NsAttachmentTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * 敏捷步伐状态类。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
@Getter @Setter
public class NimbleStepsState implements INBTSerializable<CompoundTag> {
	public static final StreamCodec<ByteBuf, NimbleStepsState> STREAM_CODEC = StreamCodec.of(
		(buf, attachment) -> {
			buf.writeBoolean(attachment.isCrawling);

			buf.writeInt(attachment.swimmingBoostCooldown);

			buf.writeInt(attachment.slideCooldown);
			buf.writeInt(attachment.slideDuration);

			buf.writeInt(attachment.landingRollWindow);

			buf.writeInt(attachment.wallRunDuration);
			buf.writeInt(attachment.wallRunCount);

			buf.writeBoolean(attachment.isWallSliding);
			buf.writeInt(attachment.wallSlideJumpReleaseGraceTicks);
			buf.writeInt(attachment.wallSlideDirection);

			buf.writeBoolean(attachment.hasJumped);
			buf.writeInt(attachment.lastWallJumpDirection);
			buf.writeInt(attachment.ticksSinceLastJump);

			buf.writeBoolean(attachment.isArmHanging);
			buf.writeInt(attachment.armHangingDirection);

			buf.writeInt(attachment.wallClimbDuration);
			buf.writeBoolean(attachment.hasWallClimbed);
		},
		(buf) -> {
			NimbleStepsState attachment = new NimbleStepsState();
			attachment.isCrawling = buf.readBoolean();

			attachment.swimmingBoostCooldown = buf.readInt();

			attachment.slideCooldown = buf.readInt();
			attachment.slideDuration = buf.readInt();

			attachment.landingRollWindow = buf.readInt();

			attachment.wallRunDuration = buf.readInt();
			attachment.wallRunCount = buf.readInt();

			attachment.isWallSliding = buf.readBoolean();
			attachment.wallSlideJumpReleaseGraceTicks = buf.readInt();
			attachment.wallSlideDirection = buf.readInt();

			attachment.hasJumped = buf.readBoolean();
			attachment.lastWallJumpDirection = buf.readInt();
			attachment.ticksSinceLastJump = buf.readInt();

			attachment.isArmHanging = buf.readBoolean();
			attachment.armHangingDirection = buf.readInt();

			attachment.wallClimbDuration = buf.readInt();
			attachment.hasWallClimbed = buf.readBoolean();

			return attachment;
		}
	);

	/**
	 * 爬行
	 */
	private boolean isCrawling;
	/**
	 * 游泳推进
	 */
	private int swimmingBoostCooldown;
	/**
	 * 滑铲/闪避
	 */
	private int slideCooldown;
	private int slideDuration;
	public boolean isSliding() {
		return slideDuration > 0;
	}

	/**
	 * 翻滚
	 */
	private int landingRollWindow;
	/**
	 * 墙跑
	 */
	private int wallRunDuration;
	private int wallRunCount;
	public boolean isWallRunning() {
		return wallRunDuration > 0;
	}
	/**
	 * 滑墙
	 */
	private boolean isWallSliding;
	private int wallSlideJumpReleaseGraceTicks;
	private int wallSlideDirection = -1;
	/**
	 * 跳跃
	 */
	private boolean hasJumped;
	private int lastWallJumpDirection = -1;
	public void resetLastWallJumpDirection() {
		this.lastWallJumpDirection = -1;
	}
	private int ticksSinceLastJump = 100;
	/**
	 * 垂挂
	 */
	private boolean isArmHanging;
	private int armHangingDirection = -1;
	public void resetArmHangingDirection() {
		this.armHangingDirection = -1;
	}
	/**
	 * 爬墙
	 */
	private int wallClimbDuration;
	private boolean hasWallClimbed;
	public boolean isWallClimbing() {
		return wallClimbDuration > 0;
	}

	@Override
	public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("isCrawling", isCrawling);
		tag.putInt("swimmingBoostCooldown", swimmingBoostCooldown);
		tag.putInt("slideCooldown", slideCooldown);
		tag.putInt("slideDuration", slideDuration);
		tag.putInt("landingRollWindow", landingRollWindow);
		tag.putInt("wallRunDuration", wallRunDuration);
		tag.putInt("wallRunCount", wallRunCount);
		tag.putBoolean("isWallSliding", isWallSliding);
		tag.putInt("wallSlideGraceTicks", wallSlideJumpReleaseGraceTicks);
		tag.putInt("wallSlideDirection", wallSlideDirection);
		tag.putBoolean("hasJumped", hasJumped);
		tag.putInt("lastWallJumpDirection", lastWallJumpDirection);
		tag.putInt("ticksSinceLastJump", ticksSinceLastJump);
		tag.putBoolean("isArmHanging", isArmHanging);
		tag.putInt("armHangingDirection", armHangingDirection);
		tag.putInt("wallClimbDuration", wallClimbDuration);
		tag.putBoolean("hasWallClimbed", hasWallClimbed);
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
		isCrawling = nbt.getBoolean("isCrawling");
		swimmingBoostCooldown = nbt.getInt("swimmingBoostCooldown");
		slideCooldown = nbt.getInt("slideCooldown");
		slideDuration = nbt.getInt("slideDuration");
		landingRollWindow = nbt.getInt("landingRollWindow");
		wallRunDuration = nbt.getInt("wallRunDuration");
		wallRunCount = nbt.getInt("wallRunCount");
		isWallSliding = nbt.getBoolean("isWallSliding");
		wallSlideJumpReleaseGraceTicks = nbt.getInt("wallSlideGraceTicks");
		wallSlideDirection = nbt.getInt("wallSlideDirection");
		hasJumped = nbt.getBoolean("hasJumped");
		lastWallJumpDirection = nbt.getInt("lastWallJumpDirection");
		ticksSinceLastJump = nbt.getInt("ticksSinceLastJump");
		isArmHanging = nbt.getBoolean("isArmHanging");
		armHangingDirection = nbt.getInt("armHangingDirection");
		wallClimbDuration = nbt.getInt("wallClimbDuration");
		hasWallClimbed = nbt.getBoolean("hasWallClimbed");
	}


	public static void setNimbleState(Player player, NimbleStepsState state) {
		player.setData(NsAttachmentTypes.NIMBLE_STEPS_STATE, state);
	}

	public static NimbleStepsState getNimbleState(Player player) {
		return player.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
	}
}
