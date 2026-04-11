package mod.arcomit.parkour.v2.content.init;

import mod.arcomit.parkour.ParkourMod;
import mod.arcomit.parkour.v2.core.statemachine.state.IParkourState;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * @Author: Arcomit
 * @CreateTime: 2026-03-12 11:04
 * @Description: 集中管理所有模组注册表的注册（且能够从中获取调用）
 */
@EventBusSubscriber(modid = ParkourMod.MODID)
public class PkRegistries {

	// 跑酷状态注册表
	private static final ResourceKey<Registry<IParkourState>> PARKOUR_STATE_REGISTRY_KEY	= ResourceKey.createRegistryKey(ParkourMod.prefix("parkour_state"));
	public static final Registry<IParkourState>		  PARKOUR_REGISTRY 		= new RegistryBuilder<>(PARKOUR_STATE_REGISTRY_KEY)
		.sync      (true)
		.defaultKey(ParkourMod.prefix("default"))
		.create    ();

	// 注册注册表
	@SubscribeEvent
	public static void registerRegistries(NewRegistryEvent event) {
		event.register(PARKOUR_REGISTRY);
	}
}