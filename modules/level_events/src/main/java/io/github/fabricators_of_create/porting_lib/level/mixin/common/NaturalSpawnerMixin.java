package io.github.fabricators_of_create.porting_lib.level.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.level.LevelHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import net.minecraft.world.level.chunk.ChunkGenerator;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {
	@ModifyReturnValue(method = "mobsAt", at = @At("RETURN"))
	private static WeightedRandomList<MobSpawnSettings.SpawnerData> modifyMobs(WeightedRandomList<MobSpawnSettings.SpawnerData> original, ServerLevel serverLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, MobCategory mobCategory, BlockPos blockPos, @Nullable Holder<Biome> holder) {
		return LevelHooks.getPotentialSpawns(serverLevel, mobCategory, blockPos, original);
	}
}
