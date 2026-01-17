package mod.arcomit.nimblesteps;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 服务器配置相关类。
 *
 * @author Arcomit
 * @since 2026-01-01
 */
@EventBusSubscriber(modid = NimbleStepsMod.MODID)
public class ServerConfig {
	protected static final ModConfigSpec SPEC;

	public static ModConfigSpec.BooleanValue REMOVE_SPEED_LIMITS;

	public static ModConfigSpec.BooleanValue ENABLE_OMNI_SPRINT;
	public static ModConfigSpec.BooleanValue ENABLE_CRAWL;
	public static ModConfigSpec.BooleanValue ENABLE_SLIDE;
	public static ModConfigSpec.IntValue SLIDE_COOLDOWN;
	public static ModConfigSpec.DoubleValue SLIDE_BOOST_SPEED;
	public static ModConfigSpec.BooleanValue ENABLE_TAP_STRAFING;
	public static ModConfigSpec.BooleanValue ENABLE_LANDING_ROLL;
	public static ModConfigSpec.IntValue LANDING_ROLL_WINDOW;
	public static ModConfigSpec.BooleanValue ENABLE_MOVE_SPEED_INCREASE;
	public static ModConfigSpec.DoubleValue WALK_SPEED_MULTIPLIER;
	public static ModConfigSpec.DoubleValue SPRINT_SPEED_MULTIPLIER;
	public static ModConfigSpec.BooleanValue ENABLE_STEP_HEIGHT_INCREASE;
	public static ModConfigSpec.DoubleValue WALK_STEP_HEIGHT;
	public static ModConfigSpec.DoubleValue SPRINT_STEP_HEIGHT;
	public static ModConfigSpec.BooleanValue ENABLE_SAFE_FALL_HEIGHT_INCREASE;
	public static ModConfigSpec.DoubleValue SAFE_FALL_HEIGHT;

	public static ModConfigSpec.BooleanValue ENABLE_SHALLOW_SWIMMING;
	public static ModConfigSpec.BooleanValue ENABLE_SWIMMING_BOOST;
	public static ModConfigSpec.DoubleValue SWIMMING_BOOST_SPEED_MULTIPLIER;
	public static ModConfigSpec.IntValue SWMMING_BOOST_COOLDOWN;
	public static ModConfigSpec.BooleanValue ENABLE_FREESTYLE;
	public static ModConfigSpec.BooleanValue ENABLE_STOP_SWIMMING_WHEN_IDLE;

	public static ModConfigSpec.BooleanValue CAN_CLIMB_MORE_BLOCKS;
	public static ModConfigSpec.BooleanValue ENABLE_UP_CLIMB_SPEED_INCREASE;
	public static ModConfigSpec.DoubleValue UP_CLIMB_SPEED_MULTIPLIER;
	public static ModConfigSpec.BooleanValue ENABLE_CLIMB_ACCELERATION_OVER_TIME;
	public static ModConfigSpec.DoubleValue UP_CLIMB_ACCELERATION_MULTIPLIER;
	public static ModConfigSpec.DoubleValue DOWN_CLIMB_ACCELERATION_MULTIPLIER;
	public static ModConfigSpec.BooleanValue ENABLE_DOWN_CLIMB_SPEED_INCREASE;
	public static ModConfigSpec.DoubleValue DOWN_CLIMB_SPEED_MULTIPLIER;
	public static ModConfigSpec.BooleanValue CLIMBABLE_BLOCK_NOT_SLOW_DOWN;
	public static ModConfigSpec.BooleanValue ENABLE_WALL_RUN;
	public static ModConfigSpec.IntValue WALL_RUN_DURATION;
	public static ModConfigSpec.IntValue WALL_RUN_MAX_COUNT;
	public static ModConfigSpec.BooleanValue ENABLE_WALL_SLIDE;
	public static ModConfigSpec.BooleanValue ENABLE_WALL_JUMP;
	public static ModConfigSpec.BooleanValue WALL_JUMP_RESET_WALL_RUN;
	public static ModConfigSpec.BooleanValue WALL_JUMP_RESET_WALL_CLIMB;
	public static ModConfigSpec.BooleanValue ENABLE_WALL_CLING;
	public static ModConfigSpec.DoubleValue WALL_CLING_MOVE_SPEED;
	public static ModConfigSpec.BooleanValue ENABLE_SUPPORT_WALL_JUMP;
	public static ModConfigSpec.BooleanValue SUPPORT_WALL_JUMP_RESET_WALL_CLIMB;
	public static ModConfigSpec.BooleanValue ENABLE_WALL_CLIMB;
	public static ModConfigSpec.IntValue WALL_CLIMB_DURATION;
	public static ModConfigSpec.DoubleValue WALL_CLIMB_SPEED;

	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		builder
			.comment("Online settings let you change all options related to multiplayer.")
			.translation("nimblesteps.configuration.online_settings")
			.push("online_settings");

		// 移除速度限制
		REMOVE_SPEED_LIMITS = builder
			.comment("Remove speed limits imposed by server.\nThis removes most rollbacks but makes it easier to cheat on a server. (default: true)")
			.translation("nimblesteps.configuration.online_settings.remove_speed_limits")
			.define("removeSpeedLimits", true);

		builder.pop();

		builder
			.comment("Locomotion settings let you change all options related to movement on the ground.")
			.translation("nimblesteps.configuration.locomotion_settings")
			.push("locomotion_settings");

		// 启用全向疾跑功能？
		ENABLE_OMNI_SPRINT = builder
			.comment("Enable omnidirectional sprinting (default: true)")
			.translation("nimblesteps.configuration.locomotion_settings.enable_omni_sprint")
			.define("enableOmniSprint", true);
		// 启用爬行功能？
		ENABLE_CRAWL = builder
			.comment("Enable crawl (default: true)")
			.translation("nimblesteps.configuration.locomotion_settings.enable_crawl")
			.define("enableCrawl", true);
		// 启用滑铲功能？
		ENABLE_SLIDE = builder
			.comment("Enable slide (default: true)")
			.translation("nimblesteps.configuration.locomotion_settings.enable_slide")
			.define("enableSlide", true);
		// 滑铲冷却时间
		SLIDE_COOLDOWN = builder
			.comment("Slide and evade cooldown in ticks (default: 12)")
			.translation("nimblesteps.configuration.locomotion_settings.slide_cooldown")
			.defineInRange("slideCooldown", 12, 0, Integer.MAX_VALUE);
		// 滑铲加速速度
		SLIDE_BOOST_SPEED = builder
			.comment("Slide boost speed (default: 0.7)")
			.translation("nimblesteps.configuration.locomotion_settings.slide_boost_speed")
			.defineInRange("slideBoostSpeed", 0.7, 0.1, 5.0);
		// 启用滑铲空中变向功能？
		ENABLE_TAP_STRAFING = builder
			.comment("Enable turning without slowdown while sliding in the air (default: true)")
			.translation("nimblesteps.configuration.locomotion_settings.enable_tap_strafing")
			.define("enableTapStrafing", true);
		// 启用着陆翻滚功能？
		ENABLE_LANDING_ROLL = builder
			.comment("Enable landing roll (default: true)")
			.translation("nimblesteps.configuration.locomotion_settings.enable_landing_roll")
			.define("enableLandingRoll", true);
		// 着陆翻滚窗口
		LANDING_ROLL_WINDOW = builder
			.comment("Landing roll window in ticks (default: 6)")
			.translation("nimblesteps.configuration.locomotion_settings.landing_roll_window")
			.defineInRange("landingRollWindow", 6, 1, Integer.MAX_VALUE);
		// 启用移速增加？
		ENABLE_MOVE_SPEED_INCREASE = builder
			.comment("Enable move speed increase (default: true)")
			.translation("nimblesteps.configuration.locomotion_settings.enable_move_speed_increase")
			.define("enableMoveSpeedIncrease", true);
		// 行走速度系数
		WALK_SPEED_MULTIPLIER = builder
			.comment("Walking speed multiplier (default: 1.3 = 30% increase)")
			.translation("nimblesteps.configuration.locomotion_settings.walk_speed_multiplier")
			.defineInRange("walkSpeedMultiplier", 1.3, 1.0, 5.0);
		// 疾跑速度系数
		SPRINT_SPEED_MULTIPLIER = builder
			.comment("Sprinting speed multiplier (default: 1.3 = 30% increase)")
			.translation("nimblesteps.configuration.locomotion_settings.sprint_speed_multiplier")
			.defineInRange("sprintSpeedMultiplier", 1.3, 1.0, 5.0);
		// 启用步高增加？
		ENABLE_STEP_HEIGHT_INCREASE = builder
			.comment("Enable step height increase (default: true)")
			.translation("nimblesteps.configuration.locomotion_settings.enable_step_height_increase")
			.define("enableStepHeightIncrease", true);
		// 行走步高
		WALK_STEP_HEIGHT = builder
			.comment("Walking step height in blocks (default: 0.6, vanilla: 0.6)")
			.translation("nimblesteps.configuration.locomotion_settings.walk_step_height")
			.defineInRange("walkStepHeight", 0.6, 0.6, 1.0);
		// 疾走步高
		SPRINT_STEP_HEIGHT = builder
			.comment("Sprinting step height in blocks (default: 1.0, vanilla: 0.6)")
			.translation("nimblesteps.configuration.locomotion_settings.sprint_step_height")
			.defineInRange("sprintStepHeight", 1.0, 0.6, 1.0);
		// 启用安全掉落高度增加？
		ENABLE_SAFE_FALL_HEIGHT_INCREASE = builder
			.comment("Enable safe fall height increase (default: true)")
			.translation("nimblesteps.configuration.locomotion_settings.enable_safe_fall_height_increase")
			.define("enableSafeFallHeightIncrease", true);
		// 安全掉落高度
		SAFE_FALL_HEIGHT = builder
			.comment("Safe fall height (default: 6.0)")
			.translation("nimblesteps.configuration.locomotion_settings.safe_fall_height")
			.defineInRange("safeFallHeight", 6.0, -3.0, Double.MAX_VALUE);

		builder.pop();

		builder
			.comment("Swimming settings let you change all options related to movement on the water.")
			.translation("nimblesteps.configuration.swimming_settings")
			.push("swimming_settings");

		// 浅水游泳启用在水中疾跑时进入游泳状态
		ENABLE_SHALLOW_SWIMMING = builder
			.comment("Enables swimming while sprinting through water (default: true)")
			.translation("nimblesteps.configuration.swimming_settings.enable_shallow_swimming")
			.define("enableShallowSwimming", true);
		// 水中推进
		ENABLE_SWIMMING_BOOST = builder
			.comment("Enables speed boost while swimming (default: true)")
			.translation("nimblesteps.configuration.swimming_settings.enable_swimming_boost")
			.define("enableSwimmingBoost", true);
		// 水中推进速度系数
		SWIMMING_BOOST_SPEED_MULTIPLIER = builder
			.comment("Swimming boost speed multiplier (default: 0.4)")
			.translation("nimblesteps.configuration.swimming_settings.swimming_boost_speed_multiplier")
			.defineInRange("swimmingBoostSpeedMultiplier", 0.4, 0.0, 5.0);
		// 水中推进冷却
		SWMMING_BOOST_COOLDOWN = builder
			.comment("Swimming boost cooldown in ticks (default: 12)")
			.translation("nimblesteps.configuration.swimming_settings.swimming_boost_cooldown")
			.defineInRange("swimmingBoostCooldown", 12, 0, Integer.MAX_VALUE);
		// 自由泳
		ENABLE_FREESTYLE = builder
			.comment("Enables freestyle swimming (default: true)")
			.translation("nimblesteps.configuration.swimming_settings.enable_freestyle")
			.define("enableFreestyle", true);
		// 启用闲置时停止游泳
		ENABLE_STOP_SWIMMING_WHEN_IDLE = builder
			.comment("Enables stopping swimming when idle (default: true)")
			.translation("nimblesteps.configuration.swimming_settings.enable_stop_swimming_when_idle")
			.define("enableStopSwimmingWhenIdle", true);

		builder.pop();

		builder
			.comment("Wall movement settings let you change all options related to movement on the wall.")
			.translation("nimblesteps.configuration.wall_movement_settings")
			.push("wall_movement_settings");

		// 可以攀爬更多的方块
		CAN_CLIMB_MORE_BLOCKS = builder
			.comment("Allows climbing more block types (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.can_climb_more_blocks")
			.define("canClimbMoreBlocks", true);
		// 启用向上攀爬速度提升
		ENABLE_UP_CLIMB_SPEED_INCREASE = builder
			.comment("Enable upward climb speed increase (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.enable_up_climb_speed_increase")
			.define("enableUpClimbSpeedIncrease", true);
		// 向上攀爬速度系数
		UP_CLIMB_SPEED_MULTIPLIER = builder
			.comment("Upward climb speed multiplier (default: 1.25)")
			.translation("nimblesteps.configuration.wall_movement_settings.up_climb_speed_multiplier")
			.defineInRange("upClimbSpeedMultiplier", 1.25, 1.0, 5.0);
		// 启用向下攀爬速度加速
		ENABLE_DOWN_CLIMB_SPEED_INCREASE = builder
			.comment("Enable downward climb speed increase when looking down (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.enable_climb_down_speed_increase")
			.define("enableClimbDownSpeedIncrease", true);
		// 向下攀爬速度系数
		DOWN_CLIMB_SPEED_MULTIPLIER = builder
			.comment("Downward climb speed multiplier (default: 2.0)")
			.translation("nimblesteps.configuration.wall_movement_settings.down_climb_speed_multiplier")
			.defineInRange("downClimbSpeedMultiplier", 2.0, 1.0, 5.0);
		// 启用攀爬随时间加速
		ENABLE_CLIMB_ACCELERATION_OVER_TIME = builder
			.comment("Enable climb acceleration over time (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.enable_climb_acceleration_over_time")
			.define("enableClimbAccelerationOverTime", true);
		// 向上攀爬增速系数
		UP_CLIMB_ACCELERATION_MULTIPLIER = builder
			.comment("Upward climb acceleration multiplier (default: 2.0)")
			.translation("nimblesteps.configuration.wall_movement_settings.up_climb_acceleration_multiplier")
			.defineInRange("upClimbAccelerationMultiplier", 2.0, 1.0, 5.0);
		// 向下攀爬增速系数
		DOWN_CLIMB_ACCELERATION_MULTIPLIER = builder
			.comment("Downward climb acceleration multiplier (default: 1.5)")
			.translation("nimblesteps.configuration.wall_movement_settings.down_climb_acceleration_multiplier")
			.defineInRange("downClimbAccelerationMultiplier", 1.5, 1.0, 5.0);
		// 穿过可攀爬的方块时不会减速
		CLIMBABLE_BLOCK_NOT_SLOW_DOWN = builder
			.comment("Climbable blocks do not slow down movement (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.climbable_block_not_slow_down")
			.define("climbableBlockNotSlowDown", true);
		// 启用墙跑
		ENABLE_WALL_RUN = builder
			.comment("Enable wall run (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.enable_wall_run")
			.define("enableWallRun", true);
		// 墙跑持续时间
		WALL_RUN_DURATION = builder
			.comment("Wall run duration in ticks (default: 30)")
			.translation("nimblesteps.configuration.wall_movement_settings.wall_run_duration")
			.defineInRange("wallRunDuration", 30, 0, Integer.MAX_VALUE);
		// 可连续墙跑的最大次数
		WALL_RUN_MAX_COUNT = builder
			.comment("Maximum consecutive wall runs (default: 1)")
			.translation("nimblesteps.configuration.wall_movement_settings.wall_run_max_count")
			.defineInRange("wallRunMaxCount", 1, 0, Integer.MAX_VALUE);
		// 启用墙滑
		ENABLE_WALL_SLIDE = builder
			.comment("Enable wall slide (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.enable_wall_slide")
			.define("enableWallSlide", true);
		// 启用蹬墙跳
		ENABLE_WALL_JUMP = builder
			.comment("Enable wall jump (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.enable_wall_jump")
			.define("enableWallJump", true);
		// 蹬墙跳重置墙跑
		WALL_JUMP_RESET_WALL_RUN = builder
			.comment("Wall jump resets wall run (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.wall_jump_reset_wall_run")
			.define("wallJumpResetWallRun", true);
		// 蹬墙跳重置爬墙
		WALL_JUMP_RESET_WALL_CLIMB = builder
			.comment("Wall jump resets wall climb (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.wall_jump_reset_wall_climb")
			.define("wallJumpResetWallClimb", true);
		// 启用垂挂
		ENABLE_WALL_CLING = builder
			.comment("Enable wall cling (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.enable_wall_cling")
			.define("enableWallCling", true);
		// 垂挂移动速度
		WALL_CLING_MOVE_SPEED = builder
			.comment("Wall cling movement speed (default: 0.15)")
			.translation("nimblesteps.configuration.wall_movement_settings.wall_cling_move_speed")
			.defineInRange("wallClingMoveSpeed", 0.15, 0.05, 1.0);
		// 启用撑墙跳
		ENABLE_SUPPORT_WALL_JUMP = builder
			.comment("Enable support wall jump (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.enable_support_wall_jump")
			.define("enableSupportWallJump", true);
		// 撑墙跳重置爬墙
		SUPPORT_WALL_JUMP_RESET_WALL_CLIMB = builder
			.comment("Support wall jump resets wall climb (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.support_wall_jump_reset_wall_climb")
			.define("supportWallJumpResetWallClimb", true);
		// 启用爬墙
		ENABLE_WALL_CLIMB = builder
			.comment("Enable wall climb (default: true)")
			.translation("nimblesteps.configuration.wall_movement_settings.enable_wall_climb")
			.define("enableWallClimb", true);
		// 爬墙持续时间
		WALL_CLIMB_DURATION = builder
			.comment("Wall climb duration in ticks (default: 20)")
			.translation("nimblesteps.configuration.wall_movement_settings.wall_climb_duration")
			.defineInRange("wallClimbDuration", 20, 0, Integer.MAX_VALUE);
		// 爬墙速度
		WALL_CLIMB_SPEED = builder
			.comment("Wall climb speed (default: 0.2)")
			.translation("nimblesteps.configuration.wall_movement_settings.wall_climb_speed")
			.defineInRange("wallClimbSpeed", 0.2, 0.05, 1.0);

		builder.pop();

		SPEC = builder.build();
	}

	public static boolean removeSpeedLimits;

	public static boolean enableOmniSprint;
	public static boolean enableCrawl;
	public static boolean enableSlide;
	public static int slideCooldown;
	public static double slideBoostSpeed;
	public static boolean enableTapStrafing;
	public static boolean enableLandingRoll;
	public static int landingRollWindow;
	public static boolean enableMoveSpeedIncrease;
	public static double walkSpeedMultiplier;
	public static double sprintSpeedMultiplier;
	public static boolean enableStepHeightIncrease;
	public static double walkStepHeight;
	public static double sprintStepHeight;
	public static boolean enableSafeFallHeightIncrease;
	public static double safeFallHeight;

	public static boolean enableShallowSwimming;
	public static boolean enableSwimmingBoost;
	public static double swimmingBoostSpeedMultiplier;
	public static int swimmingBoostCooldown;
	public static boolean enableFreestyle;
	public static boolean enableStopSwimmingWhenIdle;

	public static boolean canClimbMoreBlocks;
	public static boolean enableUpClimbSpeedIncrease;
	public static double upClimbSpeedMultiplier;
	public static boolean enableClimbAccelerationOverTime;
	public static double upClimbAccelerationMultiplier;
	public static double downClimbAccelerationMultiplier;
	public static boolean enableDownClimbSpeedIncrease;
	public static double downClimbSpeedMultiplier;
	public static boolean climbableBlockNotSlowDown;
	public static boolean enableWallRun;
	public static int wallRunDuration;
	public static int maxWallRunCount;
	public static boolean enableWallSlide;
	public static boolean enableWallJump;
	public static boolean wallJumpResetWallRun;
	public static boolean wallJumpResetWallClimb;
	public static boolean enableWallCling;
	public static double wallClingMoveSpeed;
	public static boolean enableSupportWallJump;
	public static boolean supportWallJumpResetWallClimb;
	public static boolean enableWallClimb;
	public static int wallClimbDuration;
	public static double wallClimbSpeed;

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
		if (event instanceof ModConfigEvent.Unloading) return;
		if (event.getConfig().getSpec() != SPEC) return;
		removeSpeedLimits = REMOVE_SPEED_LIMITS.get();

		enableMoveSpeedIncrease = ENABLE_MOVE_SPEED_INCREASE.get();
		enableOmniSprint = ENABLE_OMNI_SPRINT.get();
		enableCrawl = ENABLE_CRAWL.get();
		enableSlide = ENABLE_SLIDE.get();
		slideCooldown = SLIDE_COOLDOWN.get();
		slideBoostSpeed = SLIDE_BOOST_SPEED.get();
		enableTapStrafing = ENABLE_TAP_STRAFING.get();
		enableLandingRoll = ENABLE_LANDING_ROLL.get();
		landingRollWindow = LANDING_ROLL_WINDOW.get();
		walkSpeedMultiplier = WALK_SPEED_MULTIPLIER.get();
		sprintSpeedMultiplier = SPRINT_SPEED_MULTIPLIER.get();
		enableStepHeightIncrease = ENABLE_STEP_HEIGHT_INCREASE.get();
		walkStepHeight = WALK_STEP_HEIGHT.get();
		sprintStepHeight = SPRINT_STEP_HEIGHT.get();
		enableSafeFallHeightIncrease = ENABLE_SAFE_FALL_HEIGHT_INCREASE.get();
		safeFallHeight = SAFE_FALL_HEIGHT.get();

		enableShallowSwimming = ENABLE_SHALLOW_SWIMMING.get();
		enableSwimmingBoost = ENABLE_SWIMMING_BOOST.get();
		swimmingBoostSpeedMultiplier = SWIMMING_BOOST_SPEED_MULTIPLIER.get();
		swimmingBoostCooldown = SWMMING_BOOST_COOLDOWN.get();
		enableFreestyle = ENABLE_FREESTYLE.get();
		enableStopSwimmingWhenIdle = ENABLE_STOP_SWIMMING_WHEN_IDLE.get();

		canClimbMoreBlocks = CAN_CLIMB_MORE_BLOCKS.get();
		enableUpClimbSpeedIncrease = ENABLE_UP_CLIMB_SPEED_INCREASE.get();
		upClimbSpeedMultiplier = UP_CLIMB_SPEED_MULTIPLIER.get();
		enableClimbAccelerationOverTime = ENABLE_CLIMB_ACCELERATION_OVER_TIME.get();
		upClimbAccelerationMultiplier = UP_CLIMB_ACCELERATION_MULTIPLIER.get();
		downClimbAccelerationMultiplier = DOWN_CLIMB_ACCELERATION_MULTIPLIER.get();
		enableDownClimbSpeedIncrease = ENABLE_DOWN_CLIMB_SPEED_INCREASE.get();
		downClimbSpeedMultiplier = DOWN_CLIMB_SPEED_MULTIPLIER.get();
		climbableBlockNotSlowDown = CLIMBABLE_BLOCK_NOT_SLOW_DOWN.get();
		enableWallRun = ENABLE_WALL_RUN.get();
		wallRunDuration = WALL_RUN_DURATION.get();
		maxWallRunCount = WALL_RUN_MAX_COUNT.get();
		enableWallSlide = ENABLE_WALL_SLIDE.get();
		enableWallJump = ENABLE_WALL_JUMP.get();
		wallJumpResetWallRun = WALL_JUMP_RESET_WALL_RUN.get();
		wallJumpResetWallClimb = WALL_JUMP_RESET_WALL_CLIMB.get();
		enableWallCling = ENABLE_WALL_CLING.get();
		wallClingMoveSpeed = WALL_CLING_MOVE_SPEED.get();
		enableSupportWallJump = ENABLE_SUPPORT_WALL_JUMP.get();
		supportWallJumpResetWallClimb = SUPPORT_WALL_JUMP_RESET_WALL_CLIMB.get();
		enableWallClimb = ENABLE_WALL_CLIMB.get();
		wallClimbDuration = WALL_CLIMB_DURATION.get();
		wallClimbSpeed = WALL_CLIMB_SPEED.get();
	}
}
