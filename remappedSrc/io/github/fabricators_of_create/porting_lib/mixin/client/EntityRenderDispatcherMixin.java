package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.event.client.EntityAddedLayerCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.math.MathHelper;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
	@Shadow
	private Map<EntityType<?>, EntityRenderer<?>> renderers;

	@Shadow
	private Map<String, EntityRenderer<? extends PlayerEntity>> playerRenderers;

	@Inject(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLineBox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/AABB;FFFF)V", ordinal = 0, shift = At.Shift.AFTER))
	private static void port_lib$renderMultipartHitboxes(MatrixStack pMatrixStack, VertexConsumer pBuffer, Entity entity, float pPartialTicks, CallbackInfo ci) {
		if (entity instanceof MultiPartEntity pEntity && pEntity.isMultipartEntity()) {
			double d0 = -MathHelper.lerp(pPartialTicks, entity.lastRenderX, entity.getX());
			double d1 = -MathHelper.lerp(pPartialTicks, entity.lastRenderY, entity.getY());
			double d2 = -MathHelper.lerp(pPartialTicks, entity.lastRenderZ, entity.getZ());

			for(PartEntity<?> enderdragonpart : pEntity.getParts()) {
				pMatrixStack.push();
				double d3 = d0 + MathHelper.lerp(pPartialTicks, enderdragonpart.lastRenderX, enderdragonpart.getX());
				double d4 = d1 + MathHelper.lerp(pPartialTicks, enderdragonpart.lastRenderY, enderdragonpart.getY());
				double d5 = d2 + MathHelper.lerp(pPartialTicks, enderdragonpart.lastRenderZ, enderdragonpart.getZ());
				pMatrixStack.translate(d3, d4, d5);
				WorldRenderer.drawBox(pMatrixStack, pBuffer, enderdragonpart.getBoundingBox().offset(-enderdragonpart.getX(), -enderdragonpart.getY(), -enderdragonpart.getZ()), 0.25F, 1.0F, 0.0F, 1.0F);
				pMatrixStack.pop();
			}
		}
	}

	@Inject(method = "onResourceManagerReload", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$resourceReload(ResourceManager resourceManager, CallbackInfo ci, EntityRendererFactory.Context context) {
		EntityAddedLayerCallback.EVENT.invoker().addLayers(renderers, playerRenderers);
	}
}
