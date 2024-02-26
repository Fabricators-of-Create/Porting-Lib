package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.Properties.class)
public class BlockBehavior$PropertiesMixin {
	@WrapOperation(
			// isValidSpawn lambda
			method = { "method_26239", "m_vuhrtmql", "lambda$new$1" },
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I")
	)
	private static int port_lib$customLight(BlockState state, Operation<Integer> original,
											BlockState state2, BlockGetter level, BlockPos pos, EntityType<?> type) {
		if (state.getBlock() instanceof LightEmissiveBlock custom) {
			return custom.getLightEmission(state, level, pos);
		}
		return original.call(state);
	}
}
