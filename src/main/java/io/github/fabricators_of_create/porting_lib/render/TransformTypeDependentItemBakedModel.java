package io.github.fabricators_of_create.porting_lib.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;

public interface TransformTypeDependentItemBakedModel {
	BakedModel handlePerspective(TransformType type, PoseStack matrices);
}
