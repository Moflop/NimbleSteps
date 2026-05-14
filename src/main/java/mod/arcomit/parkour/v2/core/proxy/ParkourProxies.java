package mod.arcomit.parkour.v2.core.proxy;

import mod.arcomit.parkour.v2.core.proxy.api.*;
import mod.arcomit.parkour.v2.core.proxy.dummy.*;

/**
 * 跑酷模组的核心代理枢纽。
 * 状态机中所有需要跨端隔离的调用，统一从这里获取！
 *
 * @author Arcomit
 * @since 2026-04-22
 */
public class ParkourProxies {
	public static IInputProxy INPUT_PROXY = new ServerDummyInputProxy();
	public static ISoundProxy SOUND_PROXY = new ServerDummySoundProxy();
	public static IPlayerAnimProxy PLAYER_ANIM_PROXY = new ServerDummyPlayerAnimProxy();
	public static ICameraAnimProxy CAMERA_PROXY = new ServerDummyCameraAnimProxy();
	public static IPlayerServicesProxy PLAYER_SERVICES_PROXY = new ServerDummyPlayerServicesProxy();
	public static IMinecraftProxy MINECRAFT_PROXY = new ServerDummyMinecraftProxy();
}
