package io.github.fabricators_of_create.porting_lib.blocks.mixin.compat.sodium;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomDisplayFluidOverlayBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.LightEmissiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.material.FluidState;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.fabric.block.FabricBlockAccess")
public abstract class FabricBlockAccessMixin {
	@Dynamic
	@Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
	private void port_lib$usePortingLibLightEmission(BlockState state, BlockAndTintGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock) {
			cir.setReturnValue(lightEmissiveBlock.getLightEmission(state, level, pos));
		}
	}

	@Dynamic
	@Inject(method = "shouldShowFluidOverlay", at = @At("HEAD"), cancellable = true)
	private void port_lib$usePortingLibFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() instanceof CustomDisplayFluidOverlayBlock fluidOverlayBlock) {
			cir.setReturnValue(fluidOverlayBlock.shouldDisplayFluidOverlay(state, level, pos, fluidState));
		}
	}
}
