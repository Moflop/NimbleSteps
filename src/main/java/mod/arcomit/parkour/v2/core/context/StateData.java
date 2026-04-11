package mod.arcomit.parkour.v2.core.context;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.content.init.PkRegistries;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * 跑酷状态数据(不参与磁盘序列化、但网络序列化)
 *
 * @author Arcomit
 * @since 2026-03-12
 */
@NoArgsConstructor
public class StateData {

	// 核心数据 (Source of Truth)
	// 永远只存安全的基础数据类型，防止在类加载时触发注册表崩溃
	@Getter
	private ResourceLocation stateId = PkParkourStates.DEFAULT.getId();
	@Getter @Setter
	private int ticksInState = 0;
	@Getter @Setter
	private int animVariant = 0;

	// 运行时缓存 (Transient)
	// 不参与序列化，只为了避免每 Tick 都去查注册表带来的性能损耗
	private transient IParkourState cachedState = null;
	@Getter @Setter
	private transient IParkourState lastTickState = null;

	public StateData(ResourceLocation stateId, int ticksInState) {
		this.stateId = stateId != null ? stateId : PkParkourStates.DEFAULT.getId();
		this.ticksInState = ticksInState;
	}

	/**
	 * 获取当前状态实例（使用缓存机制）
	 */
	public IParkourState getState() {
		if (this.cachedState == null) {
			this.cachedState = PkRegistries.PARKOUR_REGISTRY.get(this.stateId);
			if (this.cachedState == null) {
				this.cachedState = PkParkourStates.DEFAULT.get();
			}
		}
		return this.cachedState;
	}

	/**
	 * 设置新状态时，同步更新 ID 和缓存
	 */
	public void setState(IParkourState newState) {
		this.cachedState = newState;
		ResourceLocation key = PkRegistries.PARKOUR_REGISTRY.getKey(newState);
		this.stateId = key != null ? key : PkParkourStates.DEFAULT.getId();
	}

	public static final StreamCodec<ByteBuf, StateData> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, StateData::getStateId,
		ByteBufCodecs.VAR_INT, StateData::getTicksInState,
		StateData::new
	);

	public void copyFrom(StateData other) {
		this.stateId = other.stateId;
		this.ticksInState = other.ticksInState;
		this.cachedState = other.cachedState;
	}
}