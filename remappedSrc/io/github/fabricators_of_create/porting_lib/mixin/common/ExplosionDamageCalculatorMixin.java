package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.util.ExplosionResistanceBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

@Mixin(ExplosionBehavior.class)
public abstract class ExplosionDamageCalculatorMixin {
	@Inject(method = "getBlockExplosionResistance", at = @At("HEAD"), cancellable = true)
	public void port_lib$explosionBlock(Explosion explosion, BlockView reader, BlockPos pos, BlockState state, FluidState fluid, CallbackInfoReturnable<Optional<Float>> cir) {
		if (state.getBlock() instanceof ExplosionResistanceBlock resistanceBlock)
			cir.setReturnValue(state.isAir() && fluid.isEmpty()
					? Optional.empty()
					: Optional.of(Math.max(resistanceBlock.getExplosionResistance(state, reader, pos, explosion), fluid.getBlastResistance())));
	}
}
