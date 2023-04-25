package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;

import org.joml.Vector3f;

public interface PoseStackExtensions {
	/**
	 * Pushes and applies the {@code transformation} to this pose stack. <br>
	 * The effects of this method can be reversed by a corresponding {@link PoseStack#popPose()} call.
	 *
	 * @param transformation the transformation to push
	 */
	default void pushTransformation(Transformation transformation) {
		final PoseStack self = (PoseStack) this;
		self.pushPose();

		Vector3f trans = transformation.getTranslation();
		self.translate(trans.x(), trans.y(), trans.z());

		self.mulPose(transformation.getLeftRotation());

		Vector3f scale = transformation.getScale();
		self.scale(scale.x(), scale.y(), scale.z());

		self.mulPose(transformation.getRightRotation());
	}
}
