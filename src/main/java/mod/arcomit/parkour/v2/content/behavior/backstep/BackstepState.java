package mod.arcomit.parkour.v2.content.behavior.backstep;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.content.behavior.backstep.client.BackstepClientEffect;
import mod.arcomit.parkour.v2.content.behavior.slide.SlideLogic;
import mod.arcomit.parkour.v2.content.behavior.slide.client.SlideClientEffect;
import mod.arcomit.parkour.v2.content.init.PkParkourStates;
import mod.arcomit.parkour.v2.content.init.PkSounds;
import mod.arcomit.parkour.v2.core.context.GroundData;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.context.StateData;
import mod.arcomit.parkour.v2.core.statemachine.state.AbstractParkourState;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourStateTransition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * 后撤步状态，期间无敌。
 *
 * @author Arcomit
 * @since 2026-03-26
 */
public class BackstepState extends AbstractParkourState {

	public static final int BACKSTEP_DURATION = 3; // 后撤步默认持续时间（以刻为单位）

	public BackstepState() {
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
		SlideLogic.setCooldown(player, context);
	}

	@Override
	public void onClientEnter(Player player, ParkourContext context) {
		if (FMLEnvironment.dist.isClient()) {
			BackstepClientEffect.playSound(player);
			if (player.isLocalPlayer()) {
				BackstepClientEffect.applyPhysicsAndSendPosition(player);
			}
		}
	}

	/**
	 * 校验后撤步的基础环境与玩家状态是否合法
	 */
	private boolean isBaseValid(Player player) {
		return ServerConfig.enableSlide
			&& !PlayerStateUtils.fallWillTakeDamage(player)
			&& !player.isInWater()
			&& !player.isInLava()
			&& PlayerStateUtils.isAbleToAction(player);
	}

	@Override
	public boolean canEnter(Player player) {
		GroundData groundData = ParkourContext.get(player).groundData();
		return isBaseValid(player)
			&& groundData.getSlideCooldown() <= 0;
	}

	@Override
	public boolean isValid(Player player) {
		StateData stateData = ParkourContext.get(player).stateData();
		return isBaseValid(player)
			&& stateData.getTicksInState() < BACKSTEP_DURATION;
	}
}