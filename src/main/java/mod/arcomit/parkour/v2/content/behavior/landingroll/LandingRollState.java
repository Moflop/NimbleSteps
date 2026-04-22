package mod.arcomit.parkour.v2.content.behavior.landingroll;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.behavior.landingroll.client.LandingRollClientEffect;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * 落地翻滚状态
 *
 * @author Arcomit
 * @since 2026-04-13
 */
public class LandingRollState extends AbstractParkourState {
	public static final int LANDING_ROLL_DURATION = 8;

	public LandingRollState() {
		registerTransitions(
			IParkourStateTransition.onTick(
				PkParkourStates.DEFAULT::get,
				player -> !this.isValid(player)
			)
		);
	}

	@Override
	public void onEnter(Player player, ParkourContext context) {
		super.onEnter(player, context);
		LandingRollLogic.consumeWindow(player, context);
	}

	@Override
	public void onServerEnter(Player player, ParkourContext context) {
		LandingRollLogic.addSpeedEffect(player);
	}

	@Override
	public void onClientEnter(Player player, ParkourContext context) {
		if (FMLEnvironment.dist.isClient()) {
			LandingRollClientEffect.playSound(player);
			if (player.isLocalPlayer()) {
				LandingRollClientEffect.playAnimation(player);
			}
		}
	}

	@Override
	public EntityDimensions getCustomDimensions(Player player) {
		return EntityDimensions.fixed(0.6f, 0.6f).withEyeHeight(0.4f);
	}

	@Override
	public Pose getLinkedPose() {
		return Pose.STANDING;
	}

	/**
	 * 校验落地翻滚的基础环境是否合法
	 */
	public static boolean isBaseValid(Player player) {
		return ServerConfig.enableLandingRoll
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	@Override
	public boolean canEnter(Player player) {
		GroundData groundData = ParkourContext.get(player).groundData();
		return isBaseValid(player) && groundData.getLandingRollWindow() > 0;
	}

	@Override
	public boolean isValid(Player player) {
		StateData stateData = ParkourContext.get(player).stateData();
		return isBaseValid(player) && stateData.getTicksInState() < LANDING_ROLL_DURATION;
	}
}