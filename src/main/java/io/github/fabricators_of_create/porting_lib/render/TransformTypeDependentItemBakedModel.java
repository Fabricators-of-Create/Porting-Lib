package io.github.fabricators_of_create.porting_lib.render;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;

public interface TransformTypeDependentItemBakedModel {
	BakedModel port_lib$handlePerspective(TransformType cameraTransformType);
}
