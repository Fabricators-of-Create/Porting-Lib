package io.github.fabricators_of_create.porting_lib.models;

import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public interface TransformTypeDependentItemBakedModel {
	/**
	 * Applies a transform for the given {@link ItemDisplayContext} and {@code leftHand}, and
	 * returns the model to be rendered.
	 * {@link #maybeApplyTransform(BakedModel, ItemDisplayContext, PoseStack, boolean, DefaultTransform)} should always be used, do not call directly.
	 * @param leftHand true if this item is being rendered in the player's left hand
	 * @param defaultTransform a callback which will apply the vanilla transformation on
	 */
	@OverrideOnly
	BakedModel applyTransform(ItemDisplayContext context, PoseStack poseStack, boolean leftHand, DefaultTransform defaultTransform);

	/**
	 * Attempt to apply a custom transform from the given model, unwrapping wrappers if needed.
	 * Does nothing if not a {@link TransformTypeDependentItemBakedModel}.
	 * @return null if no transformation occurred, otherwise the transformed model
	 */
	@Nullable
	static BakedModel maybeApplyTransform(BakedModel model, ItemDisplayContext context, PoseStack poseStack, boolean leftHand, DefaultTransform defaultTransform) {
		if (model instanceof TransformTypeDependentItemBakedModel transformer)
			return transformer.applyTransform(context, poseStack, leftHand, defaultTransform);

		BakedModel wrapped = model;
		while (wrapped instanceof WrapperBakedModel wrapper) {
			wrapped = wrapper.getWrappedModel();
			if (wrapped == null) {
				return null;
			} else if (wrapped instanceof TransformTypeDependentItemBakedModel transformer) {
				return transformer.applyTransform(context, poseStack, leftHand, defaultTransform);
			}
		}

		return null;
	}

	@FunctionalInterface
	interface DefaultTransform {
		void apply(BakedModel model);
	}
}
