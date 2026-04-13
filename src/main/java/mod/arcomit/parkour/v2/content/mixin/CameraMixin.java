package mod.arcomit.parkour.v2.content.mixin;

import mod.arcomit.parkour.v2.core.animation.camera.CameraAnimationController;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

	@Shadow
	public abstract Vec3 getPosition();

	@Shadow
	protected abstract void setPosition(double x, double y, double z);

	@Shadow
	public abstract float getYRot();

	@Inject(method = "setup", at = @At("TAIL"))
	public void parkour$applyCameraPositionAnimation(
		BlockGetter level,
		Entity entity,
		boolean detached,
		boolean thirdPersonReverse,
		float partialTick,
		CallbackInfo ci
	) {
		// 如果摄像机已脱离眼睛（第三人称），直接跳过不应用位移
		if (detached) {
			return;
		}

		CameraAnimationController controller = CameraAnimationController.INSTANCE;

		if (controller.isPlaying()) {
			float[] position = controller.getCurrentPosition();

			if (position[0] != 0.0f || position[1] != 0.0f || position[2] != 0.0f) {
				Vec3 currentPos = this.getPosition();
				float scale = 0.0625f;

				Vector3f moveVector = new Vector3f(
					position[0] * scale,
					position[1] * scale,
					-position[2] * scale
				);

				moveVector.rotateY(-this.getYRot() * Mth.DEG_TO_RAD);

				this.setPosition(
					currentPos.x + moveVector.x,
					currentPos.y + moveVector.y,
					currentPos.z + moveVector.z
				);
			}
		}
	}
}