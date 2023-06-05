package io.github.fabricators_of_create.porting_lib.models.mixin.client.frex;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.models.TransformTypeDependentItemBakedModel;
import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.ItemModel;
import io.vram.frex.base.renderer.context.render.ItemRenderContext;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemRenderContext.class)
public class ItemRenderContextMixin {
	@WrapWithCondition(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/ItemTransform;apply(ZLcom/mojang/blaze3d/vertex/PoseStack;)V"))
	private boolean hasCustomTransform(ItemTransform transform, boolean leftHanded, PoseStack poseStack, @Local(index = 2) ItemDisplayContext transformType, @Local(index = 8) BakedModel model, @Share("tranformed") LocalRef<BakedModel> tranformed) {
		if (model instanceof TransformTypeDependentItemBakedModel transformModel) {
			tranformed.set(transformModel.applyTransform(transformType, poseStack, leftHanded));
			return false;
		}
		tranformed.set(null);
		return true;
	}

	@ModifyReceiver(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;isCustomRenderer()Z"))
	private BakedModel useTransformedModel(BakedModel model, @Share("tranformed") LocalRef<BakedModel> tranformed) {
		BakedModel newModel = tranformed.get();
		if (newModel != null)
			return newModel;
		return model;
	}

	@ModifyReceiver(method = "renderItem", at = @At(value = "INVOKE", target = "Lio/vram/frex/api/model/ItemModel;renderAsItem(Lio/vram/frex/api/model/ItemModel$ItemInputContext;Lio/vram/frex/api/buffer/QuadSink;)V"))
	private ItemModel renderTransformedModel(ItemModel model, ItemModel.ItemInputContext input, QuadSink output, @Share("tranformed") LocalRef<BakedModel> tranformed) {
		BakedModel newModel = tranformed.get();
		if (newModel != null)
			return (ItemModel) newModel;
		return model;
	}
}
