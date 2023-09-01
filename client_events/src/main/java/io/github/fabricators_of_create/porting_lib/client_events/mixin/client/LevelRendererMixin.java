package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.DrawSelectionEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	@Shadow
	@Final
	private RenderBuffers renderBuffers;

	@Shadow
	@Final
	private Minecraft minecraft;

	@WrapWithCondition(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
	private boolean port_lib$renderBlockOutline(LevelRenderer self, PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity,
												double d, double e, double f, BlockPos blockPos, BlockState blockState,
			/* enclosing args */ PoseStack p, float partialTicks, long l, boolean bl, Camera camera,
												GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f) {
		return !DrawSelectionEvents.BLOCK.invoker().onHighlightBlock(self, camera, minecraft.hitResult, partialTicks, poseStack, renderBuffers.bufferSource());
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getModelViewStack()Lcom/mojang/blaze3d/vertex/PoseStack;", shift = At.Shift.BEFORE))
	private void port_lib$renderEntityOutline(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		HitResult hitresult = minecraft.hitResult;
		if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
			DrawSelectionEvents.ENTITY.invoker().onHighlightEntity((LevelRenderer) (Object) this, camera, hitresult, partialTick, poseStack, this.renderBuffers.bufferSource());
		}
	}
}
