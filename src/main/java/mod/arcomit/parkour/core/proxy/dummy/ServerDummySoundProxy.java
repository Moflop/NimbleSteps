package mod.arcomit.parkour.core.proxy.dummy;

import mod.arcomit.parkour.core.proxy.api.ISoundProxy;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

/**
 * TODO：描述
 *
 * @author Arcomit
 * @since 2026-04-28
 */
public class ServerDummySoundProxy implements ISoundProxy {
	@Override
	public void playEntityBoundSound(SoundEvent soundEvent, SoundSource source, float volume, float pitch, Entity entity, long seed) {}
}
