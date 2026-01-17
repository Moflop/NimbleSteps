package mod.arcomit.nimblesteps.mixin;

import mod.arcomit.nimblesteps.ServerConfig;
import mod.arcomit.nimblesteps.attachment.NimbleStepsState;
import mod.arcomit.nimblesteps.init.NsAttachmentTypes;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 本地玩家疾跑与滑铲优化Mixin。
 *
 * @author Arcomit
 * @since 2025-12-20
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends LivingEntity {

	protected LocalPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	public Input input;

	@Shadow
	@Override
	public abstract boolean isUnderWater();

	/**
	 * 全方向疾跑
	 */
	@Inject(method = "hasEnoughImpulseToStartSprinting", at = @At("HEAD"), cancellable = true)
	private void hasEnoughImpulseCanSprinting(CallbackInfoReturnable<Boolean> cir) {
		if (ServerConfig.enableOmniSprint) {
			cir.setReturnValue(this.isUnderWater() ? this.input.hasForwardImpulse() : Math.abs(this.input.forwardImpulse) >= 0.8 || Math.abs(this.input.leftImpulse) >= 0.8);
		}
	}

	/**
	 * 滑铲不减速
	 */
	@Inject(method = "isMovingSlowly", at = @At("HEAD"), cancellable = true)
	public void slideNotSlowDown(CallbackInfoReturnable<Boolean> cir) {
		NimbleStepsState nimbleStepsState = this.getData(NsAttachmentTypes.NIMBLE_STEPS_STATE);
		if (nimbleStepsState.getSlideDuration() > 0) {
			cir.setReturnValue(false);
		}
	}

	/**
	 * 撞墙不打断疾跑
	 */
	@Redirect(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;horizontalCollision:Z"))
	private boolean preventSprintInterruptionOnCollision(LocalPlayer instance) {
		return false;
	}
}
