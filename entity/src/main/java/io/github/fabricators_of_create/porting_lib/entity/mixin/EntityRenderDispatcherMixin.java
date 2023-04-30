package io.github.fabricators_of_create.porting_lib.entity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
	@Inject(
			method = "renderHitbox",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLineBox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/AABB;FFFF)V",
					ordinal = 0
			)
	)
	private static void renderMultipartHitboxes(PoseStack poseStack, VertexConsumer consumer, Entity entity, float partialTicks, CallbackInfo ci) {
		if (entity instanceof MultiPartEntity multipart && multipart.hasParts()) {
			double dX = -Mth.lerp(partialTicks, entity.xOld, entity.getX());
			double dY = -Mth.lerp(partialTicks, entity.yOld, entity.getY());
			double dZ = -Mth.lerp(partialTicks, entity.zOld, entity.getZ());

			for (PartEntity<?> part : multipart.getParts()) {
				poseStack.pushPose();
				double partDx = dX + Mth.lerp(partialTicks, part.xOld, part.getX());
				double partDy = dY + Mth.lerp(partialTicks, part.yOld, part.getY());
				double partDz = dZ + Mth.lerp(partialTicks, part.zOld, part.getZ());
				poseStack.translate(partDx, partDy, partDz);
				LevelRenderer.renderLineBox(
						poseStack, consumer,
						part.getBoundingBox().move(-part.getX(), -part.getY(), -part.getZ()),
						0.25F, 1.0F, 0.0F, 1.0F
				);
				poseStack.popPose();
			}
		}
	}
}
