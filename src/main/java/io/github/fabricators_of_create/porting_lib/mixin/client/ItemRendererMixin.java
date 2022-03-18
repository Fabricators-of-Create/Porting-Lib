package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import io.github.fabricators_of_create.porting_lib.extensions.Matrix3fExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.Matrix4fExtensions;
import io.github.fabricators_of_create.porting_lib.render.LayeredBakedModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fabricators_of_create.porting_lib.render.TransformTypeDependentItemBakedModel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = ItemRenderer.class, priority = 10000)
public abstract class ItemRendererMixin {
	@Shadow
	@Final
	private ItemModelShaper itemModelShaper;

	@Shadow
	public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource buffer, RenderType renderType, PoseStack.Pose matrixEntry) {
		return null;
	}

	@Shadow
	public static VertexConsumer getCompassFoilBuffer(MultiBufferSource buffer, RenderType renderType, PoseStack.Pose matrixEntry) {
		return null;
	}

	@Shadow
	public static VertexConsumer getFoilBufferDirect(MultiBufferSource buffer, RenderType renderType, boolean noEntity, boolean withGlint) {
		return null;
	}

	@Shadow
	public static VertexConsumer getFoilBuffer(MultiBufferSource buffer, RenderType renderType, boolean isItem, boolean glint) {
		return null;
	}

	@Shadow
	protected abstract void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack matrixStack, VertexConsumer buffer);

	@Shadow
	@Final
	private BlockEntityWithoutLevelRenderer blockEntityRenderer;

	private static final Matrix4f flipX;
	private static final Matrix3f flipXNormal;
	static {
		flipX = Matrix4f.createScaleMatrix(-1,1,1);
		flipXNormal = new Matrix3f(flipX);
	}

	// FIXME CANVAS COMPAT
	@ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
	private BakedModel port_lib$handleModel(BakedModel model, ItemStack itemStack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model1) {
		if (model instanceof TransformTypeDependentItemBakedModel handler) {
			PoseStack stack = new PoseStack();
			BakedModel bakedModel = handler.handlePerspective(transformType, stack);
			if (!stack.clear())
			{
				// Apply the transformation to the real matrix stack, flipping for left hand
				Matrix4f tMat = stack.last().pose();
				Matrix3f nMat = stack.last().normal();
				if (leftHand)
				{
					((Matrix4fExtensions)(Object)tMat).multiplyBackward(flipX);
					tMat.multiply(flipX);
					((Matrix3fExtensions)(Object)nMat).multiplyBackward(flipXNormal);
					nMat.mul(flipXNormal);
				}
				matrixStack.last().pose().multiply(tMat);
				matrixStack.last().normal().mul(nMat);
			}
			return bakedModel;
		}
		return model;
	}

	@Unique
	private void drawItemLayered(LayeredBakedModel modelIn, ItemStack itemStackIn, PoseStack matrixStackIn,
									   MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, boolean fabulous) {
		for(com.mojang.datafixers.util.Pair<BakedModel,RenderType> layerModel : modelIn.getLayerModels(itemStackIn, fabulous))
		{
			BakedModel layer = layerModel.getFirst();
			RenderType rendertype = layerModel.getSecond();
//			net.minecraftforge.client.ForgeHooksClient.setRenderType(rendertype); // neded for compatibility with MultiLayerModels
			VertexConsumer ivertexbuilder;
			if (fabulous)
			{
				ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferIn, rendertype, true, itemStackIn.hasFoil());
			} else {
				ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, rendertype, true, itemStackIn.hasFoil());
			}
			this.renderModelLists(layer, itemStackIn, combinedLightIn, combinedOverlayIn, matrixStackIn, ivertexbuilder);
		}
//		net.minecraftforge.client.ForgeHooksClient.setRenderType(null);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/ItemTransform;apply(ZLcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.BEFORE), cancellable = true)
	public void port_lib$layeredItemModel(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {

	}
}
