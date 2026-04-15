package mod.arcomit.parkour.v2.content.behavior.wallslide;

import mod.arcomit.parkour.ServerConfig;
import mod.arcomit.parkour.v1.utils.DirectionUtils;
import mod.arcomit.parkour.v1.utils.PlayerStateUtils;
import mod.arcomit.parkour.v2.core.context.ParkourContext;
import mod.arcomit.parkour.v2.core.sensor.AbstractBoxSensor;
import mod.arcomit.parkour.v2.core.sensor.SensorManager;
import mod.arcomit.parkour.v2.content.behavior.wallslide.network.BroadcastWallSlideDirS2CPayload;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;

/**
 * 滑墙核心逻辑
 *
 * @author Arcomit
 */
public class WallSlideLogic {
	private static final double WALL_ADHESION_FORCE = 0.1; // 墙面吸附力
	private static final double HORIZONTAL_SLOWDOWN = 0.8; // 水平方向减速比例
	private static final double VERTICAL_SLOWDOWN = 0.7; // 垂直方向减速比例

	/**
	 * 执行滑墙时的物理运动与速度修改
	 */
	public static void useWallSlideMovement(Player player, ParkourContext context) {
		Vec3 motion = player.getDeltaMovement();
		Direction wallDir = findAvailableWallDirection(player);

		if (wallDir != null) {
			// 修改降落速度
			Vec3 slowedMotion = motion.multiply(HORIZONTAL_SLOWDOWN, VERTICAL_SLOWDOWN, HORIZONTAL_SLOWDOWN);
			player.setDeltaMovement(slowedMotion);

			// 墙面吸附
			Vec3 wallDirVec = new Vec3(wallDir.getStepX(), 0, wallDir.getStepZ());
			Vec3 adhesionForce = wallDirVec.scale(WALL_ADHESION_FORCE);
			boolean wasOnGround = player.onGround();
			player.move(MoverType.PLAYER, adhesionForce);
			if (wasOnGround) {
				// Move方法在水平移动时会让onGround改为false，为了防止落地也无法结束滑墙需要恢复 onGround 状态
				player.setOnGround(true);
			}

			// 重置跌落伤害
			player.resetFallDistance();

			// 判断方向是否改变
			int oldDir = context.wallData().getWallSlideDirection();
			int newDir = wallDir.get3DDataValue();

			if (oldDir != newDir) {
				// 更新数据
				context.wallData().setWallSlideDirection(newDir);

				// 既然服务端也会同步执行，直接由服务端发送广播包给周围的客户端
				if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
					PacketDistributor.sendToPlayersTrackingEntity(serverPlayer,
						new BroadcastWallSlideDirS2CPayload(serverPlayer.getId(), newDir)
					);
				}
			}
		}
	}

	/**
	 * 利用 SensorManager 查找当前哪一侧的墙壁是合法的，优先检测缓存方向
	 */
	public static Direction findAvailableWallDirection(Player player) {
		SensorManager sensorManager = SensorManager.get(player);
		if (sensorManager == null) return null;

		ParkourContext context = ParkourContext.get(player);
		int cachedDirIndex = context.wallData().getWallSlideDirection();

		// 优先检测当前缓存的滑墙方向是否依然有效
		if (cachedDirIndex >= 0 && cachedDirIndex <= 5) {
			Direction cachedDir = Direction.from3DDataValue(cachedDirIndex);
			if (cachedDir != null && checkWallCollision(player, sensorManager, cachedDir)) {
				return cachedDir; // 缓存方向依然有效，直接返回
			}
		}

		// 如果缓存无效或当前没有缓存方向，则遍历所有水平方向
		ArrayList<Direction> collisionDirections = new ArrayList<>(4);

		for (Direction dir : Direction.Plane.HORIZONTAL) {
			if (checkWallCollision(player, sensorManager, dir)) {
				collisionDirections.add(dir);
			}
		}

		return DirectionUtils.getClosestDirection(player, collisionDirections);
	}

	/**
	 * 辅助方法：检测特定方向的墙壁碰撞
	 */
	private static boolean checkWallCollision(Player player, SensorManager sensorManager, Direction dir) {
		AbstractBoxSensor headSensor = sensorManager.getSensor("head_wall" + dir.getName());
		AbstractBoxSensor feetSensor = sensorManager.getSensor("feet_wall_" + dir.getName());

		return headSensor != null && feetSensor != null
			&& headSensor.isColliding(player)
			&& feetSensor.isColliding(player);
	}
}