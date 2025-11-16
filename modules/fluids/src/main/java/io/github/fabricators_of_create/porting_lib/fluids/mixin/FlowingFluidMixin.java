package io.github.fabricators_of_create.porting_lib.fluids.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import io.github.fabricators_of_create.porting_lib.fluids.extensions.ConvertToSourceFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;

import net.minecraft.world.level.material.FluidState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {
	@ModifyExpressionValue(method = "getNewLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"))
	private boolean canConvertToSource(boolean original, @Local(argsOnly = true) Level level, @Local(ordinal = 1) BlockPos pos, @Local FluidState fluidState, @Share("source") LocalBooleanRef sourceFlag) {
		if (sourceFlag.get()) {
			return original && fluidState.port_lib$canConvertToSource(level, pos);
		}
		if (fluidState.getType() instanceof ConvertToSourceFluid sourceFluid) {
			sourceFlag.set(true);
			return original && sourceFluid.canConvertToSource(fluidState, level, pos);
		}
		return original;
	}

	@ModifyExpressionValue(method = "getNewLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;canConvertToSource(Lnet/minecraft/world/level/Level;)Z"))
	private boolean checkConvertToSource(boolean original, @Share("source") LocalBooleanRef sourceFlag) {
		if (sourceFlag.get())
			return true;
		return original;
	}
}
