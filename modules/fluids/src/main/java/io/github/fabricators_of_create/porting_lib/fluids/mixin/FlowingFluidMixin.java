package io.github.fabricators_of_create.porting_lib.fluids.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FlowingFluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {
//	@ModifyExpressionValue(method = "getNewLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z")) TODO: implement
//	private boolean canConvertToSource(boolean original, @Local(index = 8) BlockPos pos) {
//
//		return original;
//	}
}
