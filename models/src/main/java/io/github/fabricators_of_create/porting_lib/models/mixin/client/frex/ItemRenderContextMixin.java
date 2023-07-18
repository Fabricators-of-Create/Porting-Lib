package io.github.fabricators_of_create.porting_lib.models.mixin.client.frex;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.models.TransformTypeDependentItemBakedModel;
import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.ItemModel;
import io.vram.frex.base.renderer.context.render.ItemRenderContext;

import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemRenderContext.class)
public class ItemRenderContextMixin {
	@WrapWithCondition(
			method = "renderItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/model/ItemTransform;apply(ZLcom/mojang/blaze3d/vertex/PoseStack;)V"
			)
	)
	private boolean applyCustomTransforms(ItemTransform transform, boolean leftHanded, PoseStack poseStack,
										  ItemModelShaper models, ItemStack stack, ItemDisplayContext renderMode,
										  boolean isLeftHand, PoseStack poseStack2, MultiBufferSource vertexConsumers,
										  int light, int overlay, BakedModel model,
										  @Share("transformed") LocalRef<BakedModel> transformedRef) {
		BakedModel transformed = TransformTypeDependentItemBakedModel.maybeApplyTransform(model, renderMode, poseStack, leftHanded);
		transformedRef.set(transformed);
		return transformed == null; // no transformation occurred, use vanilla logic
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
		BakedModel transformed = transformedRef.get();
		if (transformed != null)
			return transformed;
		return model;
	}
}
