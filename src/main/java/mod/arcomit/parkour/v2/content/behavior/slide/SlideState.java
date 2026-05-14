package mod.arcomit.parkour.v2.content.behavior.slide;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.behavior.slide.client.ClientSlideLogic;
import mod.arcomit.parkour.v2.content.init.ParkourStates;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.proxy.ParkourProxies;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 滑铲状态。
 *
 * @author Arcomit
 * @since 2026-03-26
 */
public class SlideState extends AbstractParkourState {
	public static final int SLIDE_DURATION = 10;

	public SlideState() {
		registerTransitions(
			IParkourStateTransition.onLocalTick(
				ParkourStates.DEFAULT::get,
				(player, context) -> ParkourProxies.INPUT_PROXY.getDown(player)
			)
		);
	}

	@Override
	public void onEnter(Player player, ParkourContext context) {
		super.onEnter(player, context);
		SlideLogic.setCooldown(player, context);
	}

	@Override
	public void onClientEnter(Player player, ParkourContext context) {
		ClientSlideLogic.playSound(player);
		if (player.isLocalPlayer()) {
			ClientSlideLogic.applyPhysicsAndSendPosition(player);
		}
	}

	@Override
	public EntityDimensions getCustomDimensions(Player player) {
		return EntityDimensions.fixed(0.6f, 0.6f).withEyeHeight(0.4f);
	}

	@Override
	public int generateVariant(Player player) {
		return ThreadLocalRandom.current().nextInt(2);
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	/**
	 * 校验滑铲的基础环境是否合法
	 */
	public static boolean isBaseValid(Player player) {
		return ParkourConfig.enableSlide
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToBehavior(player);
	}

	@Override
	public boolean canEnter(Player player, ParkourContext context) {
		GroundData groundData = context.groundData();
		return isBaseValid(player) && groundData.getSlideCooldown() <= 0;
	}

	@Override
	public boolean isValid(Player player, ParkourContext context) {
		StateData stateData = context.stateData();
		return isBaseValid(player) && stateData.getTicksInState() < SLIDE_DURATION;
	}
}