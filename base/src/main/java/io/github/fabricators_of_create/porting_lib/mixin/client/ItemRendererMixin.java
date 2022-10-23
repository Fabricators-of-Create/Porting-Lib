package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.RenderType;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

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
//	@ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
//	private BakedModel port_lib$handleModel(BakedModel model, ItemStack itemStack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model1) {
//		if (model instanceof TransformTypeDependentItemBakedModel handler) {
//			PoseStack stack = new PoseStack();
//			BakedModel bakedModel = handler.handlePerspective(transformType, stack);
//			if (!stack.clear())
//			{
//				// Apply the transformation to the real matrix stack, flipping for left hand
//				Matrix4f tMat = stack.last().pose();
//				Matrix3f nMat = stack.last().normal();
//				if (leftHand)
//				{
//					tMat.multiplyBackward(flipX);
//					tMat.multiply(flipX);
//					nMat.multiplyBackward(flipXNormal);
//					nMat.mul(flipXNormal);
//				}
//				matrixStack.last().pose().multiply(tMat);
//				matrixStack.last().normal().mul(nMat);
//			}
//			return bakedModel;
//		}
//		return model;
//	}
}
