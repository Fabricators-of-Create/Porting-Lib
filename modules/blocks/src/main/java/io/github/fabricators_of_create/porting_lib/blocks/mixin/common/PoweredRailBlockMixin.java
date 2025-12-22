package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomRailDirectionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PoweredRailBlock.class)
public class PoweredRailBlockMixin {
	@WrapOperation(method = "findPoweredRailSignal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	private Comparable getRailShape(BlockState instance, Property property, Operation<Comparable> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock)
			return railBlock.getRailDirection(instance, level, pos, null);
		return original.call(instance, property);
	}

	@WrapOperation(method = "isSameRailWithPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0))
	private Comparable getRailShape2(BlockState instance, Property property, Operation<Comparable> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock)
			return railBlock.getRailDirection(instance, level, pos, null);
		return original.call(instance, property);
	}

	@WrapOperation(method = "updateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 1))
	private Comparable getRailShape3(BlockState instance, Property property, Operation<Comparable> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock)
			return railBlock.getRailDirection(instance, level, pos, null);
		return original.call(instance, property);
	}
}
