package io.github.fabricators_of_create.porting_lib.render;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.extensions.TransformationExtensions;
import io.github.fabricators_of_create.porting_lib.util.TransformationHelper;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;

public interface TransformTypeDependentItemBakedModel {
	default BakedModel handlePerspective(TransformType type, PoseStack stack) {
		Transformation tr = TransformationHelper.toTransformation(((BakedModel) this).getTransforms().getTransform(type));
		if(!((TransformationExtensions)(Object)tr).isIdentity()) {
			((TransformationExtensions)(Object)tr).push(stack);
		}
		return (BakedModel) this;
	}
}
