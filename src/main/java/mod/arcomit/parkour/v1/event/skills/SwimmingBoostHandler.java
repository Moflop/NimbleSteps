//package mod.arcomit.nimblesteps.event.skills;
//
//import mod.arcomit.nimblesteps.ServerConfig;
//import mod.arcomit.nimblesteps.NimbleStepsMod;
//import mod.arcomit.nimblesteps.v1.network.serverbound.swimmingboost.ServerboundUseSwimmingBoostPacket;
//import mod.arcomit.nimblesteps.v1.utils.PlayerStateUtils;
//import mod.arcomit.nimblesteps.v2.content.client.NsKeyBindings;
//import mod.arcomit.nimblesteps.v2.content.client.NsKeyMapping;
//import mod.arcomit.nimblesteps.v2.content.client.event.InputJustPressedEvent;
//import mod.arcomit.nimblesteps.v2.content.context.MovementStateContext;
//import mod.arcomit.nimblesteps.v2.content.context.SwimMovementData;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.phys.Vec3;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.api.distmarker.OnlyIn;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.event.tick.PlayerTickEvent;
//import net.neoforged.neoforge.network.PacketDistributor;
//
///**
// * 水中推进处理器。
// *
// * @author Arcomit
// * @since 2026-01-04
// */
//@EventBusSubscriber(modid = NimbleStepsMod.MODID)
//public class SwimmingBoostHandler {
//	private static final double ZERO_THRESHOLD = 1.0E-7; // 零阈值
//
//	public static final float SWIMMING_BOOST_SOUND_VOLUME = 0.9f;
//	public static final float SWIMMING_BOOST_SOUND_PITCH = 0.8f;
//
//	@OnlyIn(Dist.CLIENT)
//	@SubscribeEvent
//	public static void trySwimmingBoostOnInput(InputJustPressedEvent event) {
//		NsKeyMapping key = event.getKeyMapping();
//		if (key != NsKeyBindings.SLIDE_KEY) {
//			return;
//		}
//
//		LocalPlayer player = Minecraft.getInstance().player;
//		if (player == null) {
//			return;
//		}
//
//		SwimMovementData swimData = MovementStateContext.get(player).getSwimData();
//		if (!canSwimmingBoost(player, swimData)) {
//			return;
//		}
//
//		useSwimmingBoost(player, swimData);
//		PacketDistributor.sendToServer(new ServerboundUseSwimmingBoostPacket());
//		Minecraft.getInstance().getSoundManager().play(
//			new EntityBoundSoundInstance(
//				SoundEvents.AMBIENT_UNDERWATER_ENTER,
//				SoundSource.PLAYERS,
//				SWIMMING_BOOST_SOUND_VOLUME,
//				SWIMMING_BOOST_SOUND_PITCH,
//				player,
//				player.getRandom().nextLong()));
//	}
//
//	public static void useSwimmingBoost(Player player, SwimMovementData swimData) {
//		swimData.setSwimmingBoostCooldown(ServerConfig.swimmingBoostCooldown);
//		Vec3 deltaMovement = player.getDeltaMovement();
//
//		Vec3 boostDirection;
//		boolean isMoving = deltaMovement.lengthSqr() >= ZERO_THRESHOLD;
//		if (isMoving) {
//			boostDirection = deltaMovement.normalize();
//		}else {
//			boostDirection = player.getLookAngle();
//		}
//
//		Vec3 boostVelocity = boostDirection.scale(ServerConfig.swimmingBoostSpeedMultiplier);
//		player.setDeltaMovement(deltaMovement.add(boostVelocity));
//	}
//
//	public static boolean canSwimmingBoost(Player player, SwimMovementData swimData) {
//		return ServerConfig.enableSwimmingBoost
//			&& swimData.getSwimmingBoostCooldown() <= 0
//			&& player.isSwimming()
//			&& PlayerStateUtils.isAbleToAction(player);
//	}
//}
