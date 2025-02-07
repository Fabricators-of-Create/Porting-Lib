package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Iterator;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import io.github.fabricators_of_create.porting_lib.event.client.FogEvents;
import io.github.fabricators_of_create.porting_lib.event.client.DrawSelectionEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import io.github.fabricators_of_create.porting_lib.block.CullingBlockEntityIterator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class LevelRendererMixin {
	@Shadow
	private Frustum cullingFrustum;

	@Shadow
	@Nullable
	private Frustum capturedFrustum;

	@Shadow
	@Final
	private BufferBuilderStorage renderBuffers;

	@Shadow
	@Final
	private MinecraftClient minecraft;

	@ModifyVariable(
		method = "renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lcom/mojang/math/Matrix4f;)V",
		slice = @Slice(
				from = @At(
						value = "INVOKE",
						target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;getRenderableBlockEntities()Ljava/util/List;"
				),
				to = @At(
						value = "INVOKE",
						target = "Lnet/minecraft/client/renderer/OutlineBufferSource;endOutlineBatch()V"
				)
		),
		at = @At("STORE")
	)
	private Iterator<BlockEntity> port_lib$wrapBlockEntityIterator(Iterator<BlockEntity> iterator) {
		return new CullingBlockEntityIterator(iterator, capturedFrustum != null ? capturedFrustum : cullingFrustum);
	}

	@WrapWithCondition(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
	private boolean port_lib$renderBlockOutline(WorldRenderer self, MatrixStack poseStack, VertexConsumer vertexConsumer, Entity entity,
										double d, double e, double f, BlockPos blockPos, BlockState blockState,
										/* enclosing args */ MatrixStack p, float partialTicks, long l, boolean bl, Camera camera,
										GameRenderer gameRenderer, LightmapTextureManager lightTexture, Matrix4f matrix4f) {
		return !DrawSelectionEvents.BLOCK.invoker().onHighlightBlock(self, camera, minecraft.crosshairTarget, partialTicks, poseStack, renderBuffers.getEntityVertexConsumers());
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getModelViewStack()Lcom/mojang/blaze3d/vertex/PoseStack;", shift = At.Shift.BEFORE))
	private void port_lib$renderEntityOutline(MatrixStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		HitResult hitresult = minecraft.crosshairTarget;
		if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
			DrawSelectionEvents.ENTITY.invoker().onHighlightEntity((WorldRenderer) (Object) this, camera, hitresult, partialTick, poseStack, this.renderBuffers.getEntityVertexConsumers());
		}
	}

	@Inject(
			method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void port_lib$lightLevel(BlockRenderView level, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if(!state.hasEmissiveLighting(level, pos) && state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock) {
			int i = level.getLightLevel(LightType.SKY, pos);
			int j = level.getLightLevel(LightType.BLOCK, pos);
			int k = lightEmissiveBlock.getLightEmission(state, level, pos);
			if (j < k) {
				j = k;
			}

			cir.setReturnValue(i << 20 | j << 4);
		}
	}

	@Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V", shift = At.Shift.AFTER))
	private void port_lib$fogRender(MatrixStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean bl, Runnable skyFogSetup, CallbackInfo ci) {
		FogEvents.RENDER_FOG.invoker().onFogRender(BackgroundRenderer.FogType.FOG_SKY, camera, partialTick, MinecraftClient.getInstance().gameRenderer.getViewDistance());
	}

	@Inject(
			method = "renderLevel",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/FogRenderer;setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZ)V",
					shift = At.Shift.AFTER
			)
	)
	private void port_lib$fogRenderAfter(MatrixStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		FogEvents.RENDER_FOG.invoker().onFogRender(BackgroundRenderer.FogType.FOG_TERRAIN, camera, partialTick, Math.max(gameRenderer.getViewDistance(), 32.0F));
	}
}
