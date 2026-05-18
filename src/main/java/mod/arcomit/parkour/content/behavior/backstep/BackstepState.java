package mod.arcomit.parkour.content.behavior.backstep;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.utils.PlayerStateUtils;
import mod.arcomit.parkour.content.behavior.backstep.client.ClientBackstepVelocity;
import mod.arcomit.parkour.content.behavior.backstep.client.ClientBackstepSound;
import mod.arcomit.parkour.content.behavior.slide.SlideLogic;
import mod.arcomit.parkour.core.context.GroundData;
import mod.arcomit.parkour.core.context.ParkourContext;
import mod.arcomit.parkour.core.context.StateData;
import mod.arcomit.parkour.core.statemachine.state.AbstractParkourState;
import net.minecraft.world.entity.player.Player;

/**
 * 后撤步状态，期间无敌。
 *
 * @author Arcomit
 * @since 2026-03-26
 */
public class BackstepState extends AbstractParkourState {

	public static final int BACKSTEP_DURATION = 3; // 后撤步默认持续时间（以刻为单位）

	@Override
	public void onEnter(Player player, ParkourContext context) {
		super.onEnter(player, context);
		SlideLogic.setCooldown(player, context);
	}

	@Override
	public void onClientEnter(Player player, ParkourContext context) {
		ClientBackstepSound.playSound(player);
		if (player.isLocalPlayer()) {
			ClientBackstepVelocity.applyVelocityAndSendPosition(player);
		}
	}

	/**
	 * 校验后撤步的基础环境与玩家状态是否合法
	 */
	private boolean isBaseValid(Player player) {
		return ParkourConfig.enableSlide
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToBehavior(player);
	}

	@Override
	public boolean canEnter(Player player, ParkourContext context) {
		GroundData groundData = context.groundData();
		return isBaseValid(player)
			&& groundData.getSlideCooldown() <= 0;
	}

	@Override
	public boolean isValid(Player player, ParkourContext context) {
		StateData stateData = context.stateData();
		return isBaseValid(player)
			&& stateData.getTicksInState() < BACKSTEP_DURATION;
	}
}