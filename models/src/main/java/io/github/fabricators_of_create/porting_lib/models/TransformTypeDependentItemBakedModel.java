package io.github.fabricators_of_create.porting_lib.models;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public interface TransformTypeDependentItemBakedModel {
	/**
	 * Applies a transform for the given {@link ItemDisplayContext} and {@code applyLeftHandTransform}, and
	 * returns the model to be rendered.
	 */
	default BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
		((BakedModel) this).getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
		return (BakedModel) this;
	}
}
