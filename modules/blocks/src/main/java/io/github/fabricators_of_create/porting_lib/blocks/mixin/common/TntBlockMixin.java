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
	@WrapOperation(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
	private boolean onPlaceExplode(Level level, BlockPos pos, Operation<Boolean> original, BlockState state) {
		if (state.getBlock() instanceof FlammableBlock block)
			return block.onCaughtFire(state, level, pos, null, null);
		return original.call(level, pos);
	}

	@WrapOperation(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
	private boolean neighborChangedExplode(Level level, BlockPos pos, Operation<Boolean> original, BlockState state) {
		if (state.getBlock() instanceof FlammableBlock block)
			return block.onCaughtFire(state, level, pos, null, null);
		return original.call(level, pos);
	}

	@WrapOperation(method = "playerWillDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
	private boolean playerWillDestroyExplode(Level level, BlockPos pos, Operation<Boolean> original, @Local(argsOnly = true) BlockState state) {
		if (state.getBlock() instanceof FlammableBlock block)
			return block.onCaughtFire(state, level, pos, null, null);
		return original.call(level, pos);
	}

	@WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)Z"))
	private boolean useItemOnExplode(Level level, BlockPos pos, LivingEntity entity, Operation<Boolean> original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Player player, @Local(argsOnly = true) BlockHitResult hitResult) {
		if (state.getBlock() instanceof FlammableBlock block)
			return block.onCaughtFire(state, level, pos, hitResult.getDirection(), player);
		return original.call(level, pos, entity);
	}

	@WrapOperation(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)Z"))
	private boolean onProjectileHitExplode(Level level, BlockPos pos, LivingEntity entity, Operation<Boolean> original, @Local(argsOnly = true) BlockState state) {
		if (state.getBlock() instanceof FlammableBlock block)
			return block.onCaughtFire(state, level, pos, null, entity);
		return original.call(level, pos, entity);
	}
}
