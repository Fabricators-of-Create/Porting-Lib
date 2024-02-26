package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.ExplosionResistanceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ExplosionDamageCalculator.class)
public abstract class ExplosionDamageCalculatorMixin {
	@Inject(method = "getBlockExplosionResistance", at = @At("HEAD"), cancellable = true)
	public void port_lib$explosionBlock(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid, CallbackInfoReturnable<Optional<Float>> cir) {
		if (state.getBlock() instanceof ExplosionResistanceBlock resistanceBlock)
			cir.setReturnValue(state.isAir() && fluid.isEmpty()
					? Optional.empty()
					: Optional.of(Math.max(resistanceBlock.getExplosionResistance(state, reader, pos, explosion), fluid.getExplosionResistance())));
	}
}
