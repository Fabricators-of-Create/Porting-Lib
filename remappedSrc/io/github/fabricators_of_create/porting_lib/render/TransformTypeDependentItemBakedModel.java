package io.github.fabricators_of_create.porting_lib.render;

import io.github.fabricators_of_create.porting_lib.util.TransformationHelper;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.AffineTransformation;

public interface TransformTypeDependentItemBakedModel {
	default BakedModel handlePerspective(Mode type, MatrixStack stack) {
		AffineTransformation tr = TransformationHelper.toTransformation(((BakedModel) this).getTransformation().getTransformation(type));
		if(!tr.isIdentity()) {
			tr.push(stack);
		}
		return (BakedModel) this;
	}
}
