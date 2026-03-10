package mod.arcomit.nimblesteps.v2.content.context;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import mod.arcomit.nimblesteps.v1.init.NsAttachmentTypes;
import mod.arcomit.nimblesteps.v2.core.sensor.sensor.SensorManager;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

/**
 * 敏捷步伐状态类。
 *
 * @author Arcomit
 * @since 2025-12-21
 */
@Getter
public class MovementStateContext {

	private final GroundMovementData groundData = new GroundMovementData();
	private final WallMovementData wallData = new WallMovementData();
	private final JumpMovementData jumpData = new JumpMovementData();
	private final SwimMovementData swimData = new SwimMovementData();
	private final InputData inputData = new InputData();
	private final SensorManager sensorManager = new SensorManager();

	public MovementStateContext() {
		//sensorManager.register();
	}

	public static final Codec<MovementStateContext> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			GroundMovementData.CODEC.optionalFieldOf("ground", new GroundMovementData()).forGetter(MovementStateContext::getGroundData),
			WallMovementData.CODEC.optionalFieldOf("wall", new WallMovementData()).forGetter(MovementStateContext::getWallData),
			JumpMovementData.CODEC.optionalFieldOf("jump", new JumpMovementData()).forGetter(MovementStateContext::getJumpData),
			SwimMovementData.CODEC.optionalFieldOf("swim", new SwimMovementData()).forGetter(MovementStateContext::getSwimData),
			InputData.CODEC.optionalFieldOf("input", new InputData()).forGetter(MovementStateContext::getInputData)
		).apply(instance, MovementStateContext::createFrom)
	);

	public static final StreamCodec<ByteBuf, MovementStateContext> STREAM_CODEC = StreamCodec.composite(
		GroundMovementData.STREAM_CODEC, MovementStateContext::getGroundData,
		WallMovementData.STREAM_CODEC, MovementStateContext::getWallData,
		JumpMovementData.STREAM_CODEC, MovementStateContext::getJumpData,
		SwimMovementData.STREAM_CODEC, MovementStateContext::getSwimData,
		InputData.STREAM_CODEC, MovementStateContext::getInputData,
		MovementStateContext::createFrom
	);

	private static MovementStateContext createFrom(GroundMovementData ground, WallMovementData wall, JumpMovementData jump, SwimMovementData swim, InputData input) {
		MovementStateContext context = new MovementStateContext();
		context.groundData.copyFrom(ground);
		context.wallData.copyFrom(wall);
		context.jumpData.copyFrom(jump);
		context.swimData.copyFrom(swim);
		context.inputData.copyFrom(input);
		return context;
	}

	public static void set(Player player, MovementStateContext state) {
		player.setData(NsAttachmentTypes.MOVEMENT_STATE_CONTEXT, state);
	}

	public static MovementStateContext get(Player player) {
		return player.getData(NsAttachmentTypes.MOVEMENT_STATE_CONTEXT);
	}
}