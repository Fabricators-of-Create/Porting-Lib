package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomRailDirectionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BaseRailBlock.class)
public class BaseRailBlockMixin {
	@WrapOperation(method = "getShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	private Comparable getRailShape(BlockState instance, Property property, Operation<Comparable> original, BlockState state, BlockGetter level, BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock)
			return railBlock.getRailDirection(instance, level, pos, null);
		return original.call(instance, property);
	}

	@WrapOperation(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	private Comparable getRailShape2(BlockState instance, Property property, Operation<Comparable> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock)
			return railBlock.getRailDirection(instance, level, pos, null);
		return original.call(instance, property);
	}

	@WrapOperation(method = "updateDir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	private Comparable getRailShape3(BlockState instance, Property property, Operation<Comparable> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock)
			return railBlock.getRailDirection(instance, level, pos, null);
		return original.call(instance, property);
	}

	@WrapOperation(method = "affectNeighborsAfterRemoval", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"))
	private Comparable getRailShape4(BlockState instance, Property property, Operation<Comparable> original, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) BlockPos pos) {
		if (instance.getBlock() instanceof CustomRailDirectionBlock railBlock)
			return railBlock.getRailDirection(instance, level, pos, null);
		return original.call(instance, property);
	}
}
