package io.github.fabricators_of_create.porting_lib.blocks.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.blocks.CullingBlockEntityIterator;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.LightEmissiveBlock;
import net.minecraft.client.renderer.LevelRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Iterator;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow
	private Frustum cullingFrustum;

	@Shadow
	@Nullable
	private Frustum capturedFrustum;

	@Shadow
	@Final
	private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

	@ModifyVariable(
			method = "renderBlockEntities",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$CompiledSection;getRenderableBlockEntities()Ljava/util/List;"
					),
					to = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/level/block/entity/BlockEntity;getBlockPos()Lnet/minecraft/core/BlockPos;",
							ordinal = 1
					)
			),
			at = @At("STORE")
	)
	private Iterator<BlockEntity> wrapBlockEntityIterator(Iterator<BlockEntity> iterator) {
		return new CullingBlockEntityIterator(this.blockEntityRenderDispatcher, iterator, capturedFrustum != null ? capturedFrustum : cullingFrustum);
	}

	@WrapOperation(
			method = "getLightColor(Lnet/minecraft/client/renderer/LevelRenderer$BrightnessGetter;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"
			)
	)
	private static int customLight(BlockState instance, Operation<Integer> original, LevelRenderer.BrightnessGetter brightnessGetter, BlockAndTintGetter level, BlockState state, BlockPos pos) {
		if (instance.getBlock() instanceof LightEmissiveBlock custom) {
			return custom.getLightEmission(instance, level, pos);
		}
		return original.call(instance);
	}
}
