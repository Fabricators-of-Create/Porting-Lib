package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.models.TransformTypeDependentItemBakedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
	@WrapWithCondition(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/model/ItemTransform;apply(ZLcom/mojang/blaze3d/vertex/PoseStack;)V"
			)
	)
	private boolean applyCustomTransforms(ItemTransform transform, boolean leftHanded, PoseStack poseStack,
									   ItemStack stack, ItemDisplayContext context, boolean leftHanded2,
									   PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay,
									   BakedModel model, @Share("transformed") LocalRef<BakedModel> transformedRef) {
		BakedModel transformed = TransformTypeDependentItemBakedModel.maybeApplyTransform(model, context, poseStack, leftHanded);
		transformedRef.set(transformed);
		return transformed == null; // no transformation occurred, use vanilla logic
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
		BakedModel transformed = transformedRef.get();
		if (transformed != null)
			return transformed;
		return model;
	}
}
