package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.fabricators_of_create.porting_lib.client.dimesnion.DimensionSpecialEffectsRenderer;
import io.github.fabricators_of_create.porting_lib.event.client.DrawSelectionEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;

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

	@Shadow
	@Nullable
	private ClientLevel level;

	@Shadow
	private int ticks;

	@WrapWithCondition(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
	private boolean renderBlockOutline(LevelRenderer self, PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double camX, double camY, double camZ, BlockPos blockPos, BlockState blockState,
			/* enclosing args */DeltaTracker deltaTracker, boolean bl, Camera camera) {
		return !DrawSelectionEvents.BLOCK.invoker().onHighlightBlock(self, camera, minecraft.hitResult, deltaTracker, poseStack, renderBuffers.bufferSource());
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V"))
	private void port_lib$renderEntityOutline(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci, @Local(index = 24) PoseStack poseStack) {
		HitResult hitresult = minecraft.hitResult;
		if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
			DrawSelectionEvents.ENTITY.invoker().onHighlightEntity((LevelRenderer) (Object) this, camera, hitresult, deltaTracker, poseStack, this.renderBuffers.bufferSource());
		}
	}

	@Inject(method = "tickRain", at = @At("HEAD"), cancellable = true)
	private void port_lib$customRainTick(Camera camera, CallbackInfo ci) {
		if (level.effects() instanceof DimensionSpecialEffectsRenderer renderer)
			if (renderer.tickRain(level, ticks, camera))
				ci.cancel();
	}

	@Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
	private void renderCustomClouds(PoseStack poseStack, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float pPartialTick, double pCamX, double pCamY, double pCamZ, CallbackInfo ci) {
		if (level.effects() instanceof DimensionSpecialEffectsRenderer renderer)
			if (renderer.renderClouds(level, ticks, pPartialTick, poseStack, pCamX, pCamY, pCamZ, modelViewMatrix, projectionMatrix))
				ci.cancel();
	}

	@Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
	private void renderCustomSky(Matrix4f modelViewMatrix, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean pIsFoggy, Runnable pSkyFogSetup, CallbackInfo ci) {
		if (level.effects() instanceof DimensionSpecialEffectsRenderer renderer)
			if (renderer.renderSky(level, ticks, pPartialTick, modelViewMatrix, pCamera, pProjectionMatrix, pIsFoggy, pSkyFogSetup))
				ci.cancel();
	}

	@Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true)
	private void renderCustomWeather(LightTexture pLightTexture, float pPartialTick, double pCamX, double pCamY, double pCamZ, CallbackInfo ci) {
		if (level.effects() instanceof DimensionSpecialEffectsRenderer renderer)
			if (renderer.renderSnowAndRain(level, ticks, pPartialTick, pLightTexture, pCamX, pCamY, pCamZ))
				ci.cancel();
	}
}
