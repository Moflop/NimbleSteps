package mod.arcomit.parkour.core.proxy.client;

import mod.arcomit.parkour.core.proxy.api.ISoundProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-28
 */
@OnlyIn(Dist.CLIENT)
public class ClientSoundProxyImpl implements ISoundProxy {
	@Override
	public void playEntityBoundSound(SoundEvent soundEvent, SoundSource source, float volume, float pitch, Entity entity, long seed) {
		Minecraft.getInstance().getSoundManager().play(
			new EntityBoundSoundInstance(
				soundEvent,
				source,
				volume,
				pitch,
				entity,
				seed
			)
		);
	}
}
