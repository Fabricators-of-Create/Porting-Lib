package io.github.fabricators_of_create.porting_lib.models.mixin.client.frex;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.models.internal.TransformTypeDependentModelHelper;
import io.github.fabricators_of_create.porting_lib.models.mixin.client.ItemRendererMixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;

import io.vram.frex.base.renderer.context.render.ItemRenderContext;

import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * @see ItemRendererMixin
 */
@Mixin(ItemRenderContext.class)
public class ItemRenderContextMixin {
	@WrapOperation(
			method = "renderItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/model/ItemTransform;apply(ZLcom/mojang/blaze3d/vertex/PoseStack;)V"
			)
	)
	private void applyCustomTransforms(ItemTransform transform, boolean leftHanded, PoseStack poseStack, Operation<Void> original,
									   ItemModelShaper models, ItemStack stack, ItemDisplayContext context, boolean isLeftHand,
									   PoseStack poseStack2, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel originalModel,
									   @Share("transformed") LocalRef<BakedModel> transformedRef) {
		TransformTypeDependentModelHelper.handleTransformations(originalModel, context, leftHanded, poseStack, original, transformedRef);
	}

	@ModifyVariable(
			method = "renderItem",
			at = @At(
					value = "INVOKE",
					target = "Lio/vram/frex/api/math/MatrixStack;translate(FFF)V"
			),
			argsOnly = true
	)
	private BakedModel useTransformedModel(BakedModel model, @Share("transformed") LocalRef<BakedModel> transformedRef) {
		return TransformTypeDependentModelHelper.getTransformedModel(model, transformedRef);
	}
}
