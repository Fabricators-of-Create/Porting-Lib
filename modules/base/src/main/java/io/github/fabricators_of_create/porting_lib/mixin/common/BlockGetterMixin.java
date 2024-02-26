package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {
	@Shadow
	BlockState getBlockState(BlockPos pos);

	@WrapOperation(
			method = "getLightEmission",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"
			)
	)
	private int port_lib$customLight(BlockState state, Operation<Integer> original,
									 BlockPos pos) {
		if (state.getBlock() instanceof LightEmissiveBlock custom) {
			return custom.getLightEmission(state, (BlockGetter) this, pos);
		}
		return original.call(state);
	}
}
