package io.github.fabricators_of_create.porting_lib.level.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.level.LevelHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.LavaFluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {
	@WrapOperation(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean onFluidPlace(Level instance, BlockPos blockPos, BlockState blockState, Operation<Boolean> original, Level p_230572_, BlockPos p_230573_) {
		return original.call(instance, blockPos, LevelHooks.fireFluidPlaceBlockEvent(instance, blockPos, p_230573_, blockState));
	}

	@WrapOperation(method = "spreadTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
	private boolean onFluidPlaceSpread(LevelAccessor instance, BlockPos blockPos, BlockState blockState, int updateType, Operation<Boolean> original) {
		return original.call(instance, blockPos, LevelHooks.fireFluidPlaceBlockEvent(instance, blockPos, blockPos, blockState), updateType);
	}
}
