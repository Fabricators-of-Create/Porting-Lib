package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.NaturalSpawner;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
	@Unique
	private static double x, y, z;
	@Unique
	private static Mob mob;
	@Unique
	private static ServerLevel level;

	@Inject(
			method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V",
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;moveTo(DDDFF)V"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void port_lib$captureLocals(MobCategory mobCategory, ServerLevel serverLevel, ChunkAccess chunkAccess, BlockPos blockPos, NaturalSpawner.SpawnPredicate spawnPredicate, NaturalSpawner.AfterSpawnCallback afterSpawnCallback, CallbackInfo ci, StructureManager structureManager, ChunkGenerator chunkGenerator, int i, BlockState blockState, BlockPos.MutableBlockPos mutableBlockPos, int j, int k, int l, int m, int n, MobSpawnSettings.SpawnerData spawnerData, SpawnGroupData spawnGroupData, int o, int p, int q, double d, double e, Player player, double f, Mob capturedMob) {
		mob = capturedMob;
		level = serverLevel;
		x = d;
		y = e;
		z = f;
	}

	@ModifyExpressionValue(
			method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V",
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;isValidPositionForMob(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;D)Z"
			)
	)
	private static boolean port_lib$canSpawnEvent(boolean original) {
		if (LivingEntityEvents.CHECK_SPAWN.invoker().onCheckSpawn(mob, level, x, y, z, null, MobSpawnType.NATURAL))
			return false;
		return original;
	}
}
