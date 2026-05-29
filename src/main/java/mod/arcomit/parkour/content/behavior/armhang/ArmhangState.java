package mod.arcomit.parkour.content.behavior.armhang;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.content.init.ParkourStates;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.context.WallData;
import mod.arcomit.parkour.core.proxy.ParkourProxies;
import mod.arcomit.parkour.core.sensor.impl.ArmhangEyeSensor;
import mod.arcomit.parkour.core.sensor.impl.ArmhangTopSensor;
import mod.arcomit.parkour.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.core.statemachine.state.IParkourStateTransition;
import mod.arcomit.parkour.utils.PlayerStateUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-05-21
 */
public class ArmhangState extends AbstractParkourState {

	public ArmhangState() {
		registerTransitions(
			IParkourStateTransition.onLocalTick(
				ParkourStates.DEFAULT::get,
				(player, context) -> ParkourProxies.INPUT_PROXY.getShiftKeyDown(player)
			)
		);
	}

	@Override
	public void onEnter(Player player, ParkourContext context) {
		super.onEnter(player, context);
		WallData wallData = context.wallData();
		wallData.setArmhangDir(player.getDirection());
		ArmhangLogic.applyLevitateAndAdhesion(player, wallData);
	}

	@Override
	public void onSimulationTick(Player player, ParkourContext context) {
		WallData wallData = context.wallData();
		ArmhangLogic.applyLevitateAndAdhesion(player, wallData);
	}

	@Override
	public void onClientTick(Player player, ParkourContext context) {

	}

	private static boolean isBaseValid(Player player) {
		if (!ParkourConfig.enableArmhang || player.onClimbable()
			|| player.isInWater() || player.isInLava() ||
			!PlayerStateUtils.isAbleToBehavior(player)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canEnter(Player player, ParkourContext context) {
		if (!isBaseValid(player)) {
			return false;
		}
		if (!ArmhangEyeSensor.isValidCollision(player, player.getDirection())
			&& !ArmhangTopSensor.isValidCollision(player, player.getDirection())) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isValid(Player player, ParkourContext context) {
		if (!isBaseValid(player)) {
			return false;
		}
		WallData wallData = context.wallData();
		Direction armhangDir = wallData.getArmhangDir();
		if (armhangDir == null) {
			return false;
		}
		if (!ArmhangEyeSensor.isValidCollision(player, armhangDir)
			&& !ArmhangTopSensor.isValidCollision(player, armhangDir)) {
			return false;
		}
		return true;
	}
}
