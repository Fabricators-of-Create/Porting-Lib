package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.FlammableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.BlockHitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TntBlock.class)
public class TntBlockMixin {
	@WrapOperation(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
	private void onPlaceExplode(Level level, BlockPos pos, Operation<Void> original, BlockState state) {
		if (state.getBlock() instanceof FlammableBlock block)
			block.onCaughtFire(state, level, pos, null, null);
		else
			original.call(level, pos);
	}

	@WrapOperation(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
	private void neighborChangedExplode(Level level, BlockPos pos, Operation<Void> original, BlockState state) {
		if (state.getBlock() instanceof FlammableBlock block)
			block.onCaughtFire(state, level, pos, null, null);
		else
			original.call(level, pos);
	}

	@WrapOperation(method = "playerWillDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
	private void playerWillDestroyExplode(Level level, BlockPos pos, Operation<Void> original, @Local(argsOnly = true) BlockState state) {
		if (state.getBlock() instanceof FlammableBlock block)
			block.onCaughtFire(state, level, pos, null, null);
		else
			original.call(level, pos);
	}

	@WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)V"))
	private void useItemOnExplode(Level level, BlockPos pos, LivingEntity entity, Operation<Void> original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Player player, @Local(argsOnly = true) BlockHitResult hitResult) {
		if (state.getBlock() instanceof FlammableBlock block)
			block.onCaughtFire(state, level, pos, hitResult.getDirection(), player);
		else
			original.call(level, pos, entity);
	}

	@WrapOperation(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)V"))
	private void onProjectileHitExplode(Level level, BlockPos pos, LivingEntity entity, Operation<Void> original, @Local(argsOnly = true) BlockState state) {
		if (state.getBlock() instanceof FlammableBlock block)
			block.onCaughtFire(state, level, pos, null, entity);
		else
			original.call(level, pos, entity);
	}
}
