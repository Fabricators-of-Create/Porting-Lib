package io.github.fabricators_of_create.porting_lib.render;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.util.TransformationHelper;
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
}
