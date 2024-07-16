package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Iterator;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import io.github.fabricators_of_create.porting_lib.event.client.DrawSelectionEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
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

	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyVariable(
		method = "renderLevel",
		slice = @Slice(
				from = @At(
						value = "INVOKE",
						target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$CompiledSection;getRenderableBlockEntities()Ljava/util/List;"
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

	@WrapOperation(
			method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"
			)
	)
	private static int port_lib$customLight(BlockState state, Operation<Integer> original,
											BlockAndTintGetter world, BlockState state2, BlockPos pos) {
		if (state.getBlock() instanceof LightEmissiveBlock custom) {
			return custom.getLightEmission(state, world, pos);
		}
		return original.call(state);
	}
}
