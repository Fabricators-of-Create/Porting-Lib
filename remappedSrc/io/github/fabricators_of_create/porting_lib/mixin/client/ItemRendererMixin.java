package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.Matrix3fExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.Matrix4fExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import io.github.fabricators_of_create.porting_lib.render.TransformTypeDependentItemBakedModel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
@Mixin(value = ItemRenderer.class, priority = 10000)
public abstract class ItemRendererMixin {
	@Shadow
	@Final
	private ItemModels itemModelShaper;

	@Shadow
	public static VertexConsumer getCompassFoilBufferDirect(VertexConsumerProvider buffer, RenderLayer renderType, MatrixStack.Entry matrixEntry) {
		return null;
	}

	@Shadow
	public static VertexConsumer getCompassFoilBuffer(VertexConsumerProvider buffer, RenderLayer renderType, MatrixStack.Entry matrixEntry) {
		return null;
	}

	@Shadow
	public static VertexConsumer getFoilBufferDirect(VertexConsumerProvider buffer, RenderLayer renderType, boolean noEntity, boolean withGlint) {
		return null;
	}

	@Shadow
	public static VertexConsumer getFoilBuffer(VertexConsumerProvider buffer, RenderLayer renderType, boolean isItem, boolean glint) {
		return null;
	}

	@Shadow
	protected abstract void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, MatrixStack matrixStack, VertexConsumer buffer);

	@Shadow
	@Final
	private BuiltinModelItemRenderer blockEntityRenderer;

	private static final Matrix4f flipX;
	private static final Matrix3f flipXNormal;
	static {
		flipX = Matrix4f.scale(-1,1,1);
		flipXNormal = new Matrix3f(flipX);
	}

	// FIXME CANVAS COMPAT
	@ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
	private BakedModel port_lib$handleModel(BakedModel model, ItemStack itemStack, ModelTransformation.Mode transformType, boolean leftHand, MatrixStack matrixStack, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay, BakedModel model1) {
		if (model instanceof TransformTypeDependentItemBakedModel handler) {
			MatrixStack stack = new MatrixStack();
			BakedModel bakedModel = handler.handlePerspective(transformType, stack);
			if (!stack.isEmpty())
			{
				// Apply the transformation to the real matrix stack, flipping for left hand
				Matrix4f tMat = stack.peek().getModel();
				Matrix3f nMat = stack.peek().getNormal();
				if (leftHand)
				{
					tMat.multiplyBackward(flipX);
					tMat.multiply(flipX);
					nMat.multiplyBackward(flipXNormal);
					nMat.multiply(flipXNormal);
				}
				matrixStack.peek().getModel().multiply(tMat);
				matrixStack.peek().getNormal().multiply(nMat);
			}
			return bakedModel;
		}
		return model;
	}
}
