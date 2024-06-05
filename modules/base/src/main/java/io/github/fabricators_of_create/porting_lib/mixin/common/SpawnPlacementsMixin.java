package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.entity.SpawnPlacementType;

import net.minecraft.world.entity.SpawnPlacementTypes;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.ValidSpawnBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(targets = "net/minecraft/world/entity/SpawnPlacementTypes$1")
public abstract class SpawnPlacementsMixin {


	@Inject(
			method = "isSpawnPositionOk",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;isValidSpawn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z"
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private void port_lib$validSpawnBlock(LevelReader levelReader, BlockPos blockPos, @Nullable EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir, BlockPos blockPos2, BlockPos blockPos3, BlockState blockState) {
		if (blockState.getBlock() instanceof ValidSpawnBlock validSpawnBlock)
			cir.setReturnValue(validSpawnBlock.isValidSpawn(blockState, levelReader, blockPos2, SpawnPlacementTypes.ON_GROUND, entityType));
	}
}
