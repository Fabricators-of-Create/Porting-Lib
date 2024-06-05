package io.github.fabricators_of_create.porting_lib.blocks.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.OnExplodedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

	@WrapOperation(method = "onExplosionHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;wasExploded(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V"))
	private void onBlockExploded(Block instance, Level level, BlockPos blockPos, Explosion explosion, Operation<Void> original, @Local(argsOnly = true) BlockState state) {
		if (state.getBlock() instanceof OnExplodedBlock onExplodedBlock) {
			onExplodedBlock.onBlockExploded(state, level, blockPos, explosion);
		} else {
			original.call(instance, level, blockPos, explosion);
		}
	}

	@WrapWithCondition(method = "onExplosionHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
	private boolean dontSetBlockOnCustom(Level level, BlockPos pos, BlockState airState, int flag, @Local(argsOnly = true) BlockState state) {
		if (state.getBlock() instanceof OnExplodedBlock)
			return false;
		return true;
	}
}
