package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

@Mixin(LightEngine.class)
public class LightEngineMixin {
	@WrapOperation(method = "hasDifferentLightProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
	private static int customLightEmissionBlock(BlockState state, Operation<Integer> operation, BlockGetter world, BlockPos pos) {
		if (state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock)
			return lightEmissiveBlock.getLightEmission(state, world, pos);
		return operation.call(state);
	}
}
