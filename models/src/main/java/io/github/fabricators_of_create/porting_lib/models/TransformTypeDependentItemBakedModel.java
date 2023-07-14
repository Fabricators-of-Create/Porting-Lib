package io.github.fabricators_of_create.porting_lib.models;

import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public interface TransformTypeDependentItemBakedModel {
	/**
	 * Applies a transform for the given {@link ItemDisplayContext} and {@code leftHand}, and
	 * returns the model to be rendered.
	 * {@link #maybeApplyTransform(BakedModel, ItemDisplayContext, PoseStack, boolean)} should always be used, do not call directly
	 * unless for super or you know what you're doing.
	 */
	@OverrideOnly
	default BakedModel applyTransform(ItemDisplayContext context, PoseStack poseStack, boolean leftHand) {
		((BakedModel) this).getTransforms().getTransform(context).apply(leftHand, poseStack);
		return (BakedModel) this;
	}

	/**
	 * Attempt to apply a custom transform from the given model, unwrapping wrappers if needed.
	 * Does nothing if not a {@link TransformTypeDependentItemBakedModel}.
	 * @return null if no transformation occurred, otherwise the transformed model
	 */
	@Nullable
	static BakedModel maybeApplyTransform(BakedModel model, ItemDisplayContext context, PoseStack poseStack, boolean leftHand) {
		if (model instanceof TransformTypeDependentItemBakedModel transformer)
			return transformer.applyTransform(context, poseStack, leftHand);

		BakedModel wrapped = model;
		while (wrapped instanceof WrapperBakedModel wrapper) {
			wrapped = wrapper.getWrappedModel();
			if (wrapped == null) {
				return null;
			} else if (wrapped instanceof TransformTypeDependentItemBakedModel transformer) {
				return transformer.applyTransform(context, poseStack, leftHand);
			}
		}

		return null;
	}
}
