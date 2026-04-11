package mod.arcomit.parkour.v2.core.context;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import mod.arcomit.parkour.v2.content.init.PkAttachmentTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

/**
 * 敏捷步伐状态类。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
public record ParkourContext(GroundData groundData, WallData wallData, JumpData jumpData, SwimData swimData,
			     InputData inputData, StateData stateData) {

	// 用于 Attachment 的默认初始化
	public ParkourContext() {
		this(new GroundData(), new WallData(), new JumpData(), new SwimData(), new InputData(), new StateData());
	}

	public static final Codec<ParkourContext> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			GroundData.CODEC.optionalFieldOf("ground", new GroundData()).forGetter(ParkourContext::groundData),
			WallData.CODEC.optionalFieldOf("wall", new WallData()).forGetter(ParkourContext::wallData),
			JumpData.CODEC.optionalFieldOf("jump", new JumpData()).forGetter(ParkourContext::jumpData),
			SwimData.CODEC.optionalFieldOf("swim", new SwimData()).forGetter(ParkourContext::swimData),
			InputData.CODEC.optionalFieldOf("input", new InputData()).forGetter(ParkourContext::inputData)
		).apply(instance,
			(ground, wall, jump, swim, input) -> new ParkourContext(ground, wall, jump, swim, input, new StateData())
		)
	);

	// TODO: 剥离InputData的服务端广播流（冗余）
	public static final StreamCodec<ByteBuf, ParkourContext> STREAM_CODEC = StreamCodec.composite(
		GroundData.STREAM_CODEC, ParkourContext::groundData,
		WallData.STREAM_CODEC, ParkourContext::wallData,
		JumpData.STREAM_CODEC, ParkourContext::jumpData,
		SwimData.STREAM_CODEC, ParkourContext::swimData,
		InputData.STREAM_CODEC, ParkourContext::inputData,
		StateData.STREAM_CODEC, ParkourContext::stateData,
		ParkourContext::new
	);

	public static void set(Player player, ParkourContext state) {
		player.setData(PkAttachmentTypes.PARKOUR_CONTEXT, state);
	}

	public static ParkourContext get(Player player) {
		return player.getData(PkAttachmentTypes.PARKOUR_CONTEXT);
	}
}