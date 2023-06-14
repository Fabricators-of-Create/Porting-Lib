package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fabricators_of_create.porting_lib.render.TransformTypeDependentItemBakedModel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(value = ItemRenderer.class, priority = 1200)
public abstract class ItemRendererMixin {
	// FIXME CANVAS COMPAT
	@ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
	private BakedModel applyCustomTransforms(BakedModel model, ItemStack itemStack, TransformType transformType,
											 boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer,
											 int combinedLight, int combinedOverlay, BakedModel model1) {
		return TransformTypeDependentItemBakedModel.maybeApplyTransform(model, transformType, matrixStack, leftHand);
	}
}
