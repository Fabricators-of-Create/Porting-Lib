package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Iterator;
import java.util.List;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.fabricators_of_create.porting_lib.block.CustomRenderBoundingBoxBlockEntity;
import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import io.github.fabricators_of_create.porting_lib.event.client.DrawSelectionEvents;
import io.github.fabricators_of_create.porting_lib.mixin_extensions.injectors.wrap_variable.WrapVariable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
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

	@ModifyExpressionValue(
		method = "renderLevel",
		at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$CompiledSection;getRenderableBlockEntities()Ljava/util/List;"
		)
	)
	private List<BlockEntity> port_lib$wrapBlockEntityList(List<BlockEntity> blockEntities) {
		return blockEntities.stream().filter(blockEntity -> {
			if (blockEntity instanceof CustomRenderBoundingBoxBlockEntity aabbBE)
				return (capturedFrustum != null ? capturedFrustum : cullingFrustum).isVisible(aabbBE.getRenderBoundingBox());
			return true;
		}).toList();
	}

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
