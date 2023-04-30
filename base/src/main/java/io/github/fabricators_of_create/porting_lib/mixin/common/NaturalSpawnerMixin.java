package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.ValidSpawnBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
	@Inject(
			method = "isSpawnPositionOk",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;isValidSpawn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z"
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private static void port_lib$validSpawnBlock(SpawnPlacements.Type placeType, LevelReader level, BlockPos pos,
												 EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir,
												 BlockState blockState, FluidState fluidState, BlockPos blockPos,
												 BlockPos blockPos2, BlockState blockState2) {
		if (blockState2.getBlock() instanceof ValidSpawnBlock validSpawnBlock)
			cir.setReturnValue(validSpawnBlock.isValidSpawn(blockState2, level, blockPos2, placeType, entityType));
	}
}
