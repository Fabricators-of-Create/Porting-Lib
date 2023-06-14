package io.github.fabricators_of_create.porting_lib.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;

public interface TransformTypeDependentItemBakedModel {
	/**
	 * Applies a transform for the given {@link ItemTransforms.TransformType} and {@code applyLeftHandTransform}, and
	 * returns the model to be rendered.
	 */
	default BakedModel applyTransform(ItemTransforms.TransformType transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
		((BakedModel) this).getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
		return (BakedModel) this;
	}

	/**
	 * Attempt to apply a custom transform from the given model, unwrapping wrappers if needed.
	 * Does nothing if not a {@link TransformTypeDependentItemBakedModel}.
	 */
	static BakedModel maybeApplyTransform(BakedModel model, TransformType type, PoseStack poseStack, boolean leftHand) {
		if (model instanceof TransformTypeDependentItemBakedModel transformer)
			return transformer.applyTransform(type, poseStack, leftHand);

		BakedModel wrapped = model;
		while (wrapped instanceof WrapperBakedModel wrapper) {
			wrapped = wrapper.getWrappedModel();
			if (wrapped == null) {
				return model;
			} else if (wrapped instanceof TransformTypeDependentItemBakedModel transformer) {
				return transformer.applyTransform(type, poseStack, leftHand);
			}
		}

		return model;
	}
}
