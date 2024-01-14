package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.BlockLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;

@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin extends LightEngine<BlockLightSectionStorage.BlockDataLayerStorageMap, BlockLightSectionStorage> {
	protected BlockLightEngineMixin(LightChunkGetter chunkProvider, BlockLightSectionStorage lightStorage) {
		super(chunkProvider, lightStorage);
	}

	@WrapOperation(method = "getEmission", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
	private int getCustomLightEmission(BlockState state, Operation<Integer> operation, long blockPos, BlockState blockState) {
		if (state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock)
			return lightEmissiveBlock.getLightEmission(state, this.chunkSource.getLevel(), BlockPos.of(blockPos));
		return operation.call(state);
	}
}
