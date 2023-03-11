package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

import java.util.function.BiConsumer;

@Mixin(TrunkPlacer.class)
public class TrunkPlacerMixin {
	@ModifyExpressionValue(method = "setDirtAt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;forceDirt:Z"))
	private static boolean shouldUseModdedDirt(boolean original, LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration pConfig) {
		return !(((net.minecraft.world.level.LevelReader) pLevel).getBlockState(pPos).onTreeGrow((net.minecraft.world.level.LevelReader) pLevel, pBlockSetter, pRandom, pPos, pConfig)) && original;
	}
}
