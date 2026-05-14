package mod.arcomit.parkour.v2.core.proxy.api;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public interface ISoundProxy {
	void playEntityBoundSound(SoundEvent soundEvent, SoundSource source, float volume, float pitch, Entity entity, long seed);
}
