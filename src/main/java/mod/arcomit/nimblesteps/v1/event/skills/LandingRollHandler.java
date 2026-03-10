package mod.arcomit.nimblesteps.event.skills;

import mod.arcomit.nimblesteps.NimbleStepsMod;
import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.v2.context.MovementStateContext;
import mod.arcomit.nimblesteps.client.NsKeyBindings;
import mod.arcomit.nimblesteps.client.NsKeyMapping;
import mod.arcomit.nimblesteps.client.event.InputJustPressedEvent;
import mod.arcomit.nimblesteps.init.NsSounds;
import mod.arcomit.nimblesteps.network.serverbound.roll.ServerboundSetLandingRollWindowPacket;
import mod.arcomit.nimblesteps.utils.PlayerStateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 落地翻滚处理器。
 *
 * @author Arcomit
 * @since 2026-01-05
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class LandingRollHandler {
	private static final float ROLL_SOUND_VOLUME = 1.0f;
	private static final float ROLL_SOUND_PITCH = 1.0f;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void tryLandingRollWindowOnInput(InputJustPressedEvent event) {
		NsKeyMapping key = event.getKeyMapping();
		if (key != NsKeyBindings.SLIDE_KEY) {
			return;
		}

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		MovementStateContext state = MovementStateContext.getNimbleState(player);
		if (!canSetLandingRollWindow(player, state)) {
			return;
		}

		state.setLandingRollWindow(ServerConfig.landingRollWindow);
		PacketDistributor.sendToServer(new ServerboundSetLandingRollWindowPacket());
	}

	@SubscribeEvent
	public static void tryLandingRollOnFall(LivingFallEvent event) {
		if (!(event.getEntity() instanceof Player player)){
			return;
		}

		if (!PlayerStateUtils.fallWillTakeDamage(player)) {
			return;
		}

		MovementStateContext state = MovementStateContext.getNimbleState(player);
		boolean landingRollWindowExpired = state.getLandingRollWindow() <= 0;
		if (landingRollWindowExpired) {
			return;
		}

		event.setDamageMultiplier(0);
		event.setCanceled(true);
		Level level = player.level();
		if (level.isClientSide) {
			Minecraft.getInstance().getSoundManager().play(
				new EntityBoundSoundInstance(
					NsSounds.LANDING_ROLL.get(),
					SoundSource.PLAYERS,
					ROLL_SOUND_VOLUME,
					ROLL_SOUND_PITCH,
					player,
					player.getRandom().nextLong()));
		} else {
			level.playSound(
				player,
				player.getX(),
				player.getY(),
				player.getZ(),
				NsSounds.LANDING_ROLL.get(),
				SoundSource.PLAYERS,
				ROLL_SOUND_VOLUME,
				ROLL_SOUND_PITCH);
			player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0));
		}
	}

	@SubscribeEvent
	public static void decrementLandingRollWindow(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		MovementStateContext state = MovementStateContext.getNimbleState(player);
		boolean landingRollWindowExpired = state.getLandingRollWindow() <= 0;
		if (landingRollWindowExpired) {
			return;
		}

		state.setLandingRollWindow(state.getLandingRollWindow() - 1);
	}

	private static boolean canSetLandingRollWindow(Player player, MovementStateContext state) {
		return ServerConfig.enableLandingRoll
			&& state.getLandingRollWindow() <= 0
			&& PlayerStateUtils.fallWillTakeDamage(player)
			&& PlayerStateUtils.isAbleToAction(player);
	}
}