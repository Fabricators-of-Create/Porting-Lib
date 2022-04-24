package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Iterator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import io.github.fabricators_of_create.porting_lib.event.FogEvents;
import io.github.fabricators_of_create.porting_lib.event.client.DrawSelectionEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

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
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	@Shadow
	private Frustum cullingFrustum;

	@Shadow
	@Nullable
	private Frustum capturedFrustum;

	@Shadow
	@Final
	private RenderBuffers renderBuffers;

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
	private Iterator<BlockEntity> wrapBlockEntityIterator(Iterator<BlockEntity> iterator) {
		return new CullingBlockEntityIterator(iterator, capturedFrustum != null ? capturedFrustum : cullingFrustum);
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getModelViewStack()Lcom/mojang/blaze3d/vertex/PoseStack;", shift = At.Shift.BEFORE))
	public void renderOutline(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		HitResult hitresult = Minecraft.getInstance().hitResult;
		if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
			DrawSelectionEvents.ENTITY.invoker().onHighlightEntity((LevelRenderer) (Object) this, camera, hitresult, partialTick, poseStack, this.renderBuffers.bufferSource());
		}
	}

	@Inject(
			method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void port_lib$lightLevel(BlockAndTintGetter level, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if(!state.emissiveRendering(level, pos) && state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock) {
			int i = level.getBrightness(LightLayer.SKY, pos);
			int j = level.getBrightness(LightLayer.BLOCK, pos);
			int k = lightEmissiveBlock.getLightEmission(state, level, pos);
			if (j < k) {
				j = k;
			}

			cir.setReturnValue(i << 20 | j << 4);
		}
	}

	@Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V", shift = At.Shift.AFTER))
	public void port_lib$fogRender(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean bl, Runnable skyFogSetup, CallbackInfo ci) {
		FogEvents.RENDER_FOG.invoker().onFogRender(FogRenderer.FogMode.FOG_SKY, camera, partialTick, Minecraft.getInstance().gameRenderer.getRenderDistance());
	}

	@Inject(
			method = "renderLevel",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/FogRenderer;setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZ)V",
					shift = At.Shift.AFTER
			)
	)
	public void port_lib$fogRenderAfter(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		FogEvents.RENDER_FOG.invoker().onFogRender(FogRenderer.FogMode.FOG_TERRAIN, camera, partialTick, Math.max(gameRenderer.getRenderDistance(), 32.0F));
	}
}
