package io.github.fabricators_of_create.porting_lib.models.testmod.client;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Axis;

import io.github.fabricators_of_create.porting_lib.models.TransformTypeDependentItemBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public class DerpyItemModel extends ForwardingBakedModel implements TransformTypeDependentItemBakedModel {
	public DerpyItemModel(BakedModel wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform, DefaultTransform defaultTransform) {
		poseStack.mulPose(Axis.YP.rotation((float) (((Minecraft.getInstance().level.getGameTime() * 66.666666666666)) / 1000.0F)));
		defaultTransform.apply(this);
		return this;
	}
}
