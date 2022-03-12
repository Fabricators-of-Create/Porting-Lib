package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Iterator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import io.github.fabricators_of_create.porting_lib.event.client.DrawSelectionEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
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
}
