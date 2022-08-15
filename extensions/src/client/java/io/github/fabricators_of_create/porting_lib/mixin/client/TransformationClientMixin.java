package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.*;

import io.github.fabricators_of_create.porting_lib.extensions.client.TransformationClientExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Transformation.class)
public abstract class TransformationClientMixin implements TransformationClientExtensions {
	@Shadow
	public abstract Vector3f getTranslation();

	@Shadow
	public abstract Quaternion getRightRotation();

	@Shadow
	public abstract Vector3f getScale();

	@Shadow
	public abstract Quaternion getLeftRotation();


	@Environment(EnvType.CLIENT)
	@Override
	public void push(PoseStack stack) {
		stack.pushPose();

		Vector3f trans = this.getTranslation();
		stack.translate(trans.x(), trans.y(), trans.z());

		stack.mulPose(this.getLeftRotation());

		Vector3f scale = this.getScale();
		stack.scale(scale.x(), scale.y(), scale.z());

		stack.mulPose(this.getRightRotation());
	}
}
