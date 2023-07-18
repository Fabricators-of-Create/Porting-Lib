package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.models.internal.TransformTypeDependentModelHelper;
import io.github.fabricators_of_create.porting_lib.models.mixin.client.frex.ItemRenderContextMixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * @see ItemRenderContextMixin
 */
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
	@WrapOperation(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/model/ItemTransform;apply(ZLcom/mojang/blaze3d/vertex/PoseStack;)V"
			)
	)
	private void applyCustomTransforms(ItemTransform transform, boolean leftHanded, PoseStack poseStack, Operation<Void> original,
									   ItemStack stack, ItemDisplayContext context, boolean leftHanded2, PoseStack matrices,
									   MultiBufferSource vertexConsumers, int light, int overlay, BakedModel originalModel,
									   @Share("transformed") LocalRef<BakedModel> transformedRef) {
		TransformTypeDependentModelHelper.handleTransformations(originalModel, context, leftHanded, poseStack, original, transformedRef);
	}

	@ModifyVariable(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
			),
			argsOnly = true
	)
	private BakedModel useTransformedModel(BakedModel model, @Share("transformed") LocalRef<BakedModel> transformedRef) {
		return TransformTypeDependentModelHelper.getTransformedModel(model, transformedRef);
	}
}
