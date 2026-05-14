package mod.arcomit.parkour.v2.content.behavior.backstep.client;

import mod.arcomit.parkour.ParkourConfig;
import mod.arcomit.parkour.v2.core.proxy.ParkourProxies;
import mod.arcomit.parkour.v2.core.proxy.api.IInputProxy;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 后撤步客户端效果
 *
 * @author Arcomit
 * @since 2026-04-22
 */
public class ClientBackstepLogic {
	private static final float BACKSTEPS_SOUND_VOLUME = 0.6f;
	private static final float BACKSTEPS_SOUND_PITCH = 1.4f;

	public static void playSound(Player player) {
		ParkourProxies.SOUND_PROXY.playEntityBoundSound(
			SoundEvents.BREEZE_SLIDE,
			SoundSource.PLAYERS,
			BACKSTEPS_SOUND_VOLUME,
			BACKSTEPS_SOUND_PITCH,
			player,
			player.getRandom().nextLong()
		);
	}

	public static void applyPhysicsAndSendPosition(Player player) {
		if (!player.isLocalPlayer()) return;
		IInputProxy input = ParkourProxies.INPUT_PROXY;
		float forwardImpulse = input.getForwardImpulse(player);
		float leftImpulse = input.getLeftImpulse(player);

		float yRotRad = player.getYRot() * Mth.DEG_TO_RAD;
		float sin = Mth.sin(yRotRad);
		float cos = Mth.cos(yRotRad);

		double motionX = leftImpulse * cos - forwardImpulse * sin;
		double motionZ = forwardImpulse * cos + leftImpulse * sin;

		Vec3 motion = new Vec3(motionX, 0, motionZ).normalize().scale(ParkourConfig.slideBoostSpeed);
		player.setDeltaMovement(player.getDeltaMovement().add(motion));
		ParkourProxies.PLAYER_SERVICES_PROXY.sendPosition(player);// 发送坐标请求服务端同步坐标
	}
}
