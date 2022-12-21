package io.github.fabricators_of_create.porting_lib.model_loader.render;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Transformation;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.generators.TransformationHelper;

public interface TransformTypeDependentItemBakedModel {
	default BakedModel handlePerspective(TransformType type, PoseStack stack) {
		Transformation tr = TransformationHelper.toTransformation(((BakedModel) this).getTransforms().getTransform(type));
		if(!tr.isIdentity()) {
			tr.push(stack);
		}
		return (BakedModel) this;
	}
}
