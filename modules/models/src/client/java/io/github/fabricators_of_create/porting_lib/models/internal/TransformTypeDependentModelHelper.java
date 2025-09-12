package io.github.fabricators_of_create.porting_lib.models.internal;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.models.TransformTypeDependentItemBakedModel;
import io.github.fabricators_of_create.porting_lib.models.TransformTypeDependentItemBakedModel.DefaultTransform;

import io.github.fabricators_of_create.porting_lib.models.mixin.client.ItemRendererMixin;

import io.github.fabricators_of_create.porting_lib.models.mixin.client.frex.ItemRenderContextMixin;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Common code used in {@link ItemRendererMixin} and {@link ItemRenderContextMixin}
 */
@Internal
public class TransformTypeDependentModelHelper {
	public static void handleTransformations(BakedModel originalModel, ItemDisplayContext ctx, boolean leftHanded, PoseStack poseStack,
											 Operation<Void> original, LocalRef<BakedModel> transformedRef) {
		DefaultTransformImpl.INSTANCE.setup(original, ctx, leftHanded, poseStack);
		BakedModel transformed = TransformTypeDependentItemBakedModel
				.maybeApplyTransform(originalModel, ctx, poseStack, leftHanded, DefaultTransformImpl.INSTANCE);

		if (transformed != null) { // transform applied
			if (transformed != originalModel) { // model changed, store new one
				transformedRef.set(transformed);
			}
		} else { // no custom transform, apply default
			DefaultTransformImpl.INSTANCE.apply(originalModel);
		}
	}

	public static BakedModel getTransformedModel(BakedModel model, LocalRef<BakedModel> transformedRef) {
		BakedModel transformed = transformedRef.get();
		return transformed != null ? transformed : model;
	}

	public enum DefaultTransformImpl implements DefaultTransform {
		INSTANCE;

		private Operation<Void> original;
		private ItemDisplayContext ctx;
		private boolean leftHanded;
		private PoseStack poseStack;

		@Override
		public void apply(BakedModel model) {
			original.call(model.getTransforms().getTransform(ctx), leftHanded, poseStack);
		}

		private void setup(Operation<Void> original, ItemDisplayContext ctx, boolean leftHanded, PoseStack poseStack) {
			this.original = original;
			this.ctx = ctx;
			this.leftHanded = leftHanded;
			this.poseStack = poseStack;
		}
	}
}
