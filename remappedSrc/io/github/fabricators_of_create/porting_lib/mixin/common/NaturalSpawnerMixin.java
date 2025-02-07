package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.util.ValidSpawnBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SpawnHelper.class)
public abstract class NaturalSpawnerMixin {
	@Shadow
	protected static boolean isValidPositionForMob(ServerWorld serverLevel, MobEntity mob, double d) {
		throw new RuntimeException();
	}

	@Inject(
			method = "isSpawnPositionOk",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;isValidSpawn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z"
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private static void port_lib$validSpawnBlock(SpawnRestriction.Location placeType, WorldView level, BlockPos pos, EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir, BlockState blockState, FluidState fluidState, BlockPos blockPos, BlockPos blockPos2, BlockState blockState2) {
		if (blockState2.getBlock() instanceof ValidSpawnBlock validSpawnBlock)
			cir.setReturnValue(validSpawnBlock.isValidSpawn(blockState2, level, blockPos2, placeType, entityType));
	}

	@Unique
	private static double x, y, z;
	@Unique
	private static MobEntity mob;
	@Unique
	private static ServerWorld level;

	@Inject(
			method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V",
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;moveTo(DDDFF)V"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void port_lib$captureLocals(SpawnGroup mobCategory, ServerWorld serverLevel, Chunk chunkAccess, BlockPos blockPos, SpawnHelper.Checker spawnPredicate, SpawnHelper.Runner afterSpawnCallback, CallbackInfo ci, StructureAccessor structureFeatureManager, ChunkGenerator chunkGenerator, int i, BlockState blockState, BlockPos.Mutable mutableBlockPos, int j, int k, int l, int m, int n, SpawnSettings.SpawnEntry spawnerData, EntityData spawnGroupData, int o, int p, int q, double d, double e, PlayerEntity player, double f, MobEntity capturedMob) {
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
		if (LivingEntityEvents.CHECK_SPAWN.invoker().onCheckSpawn(mob, level, x, y, z, null, SpawnReason.NATURAL))
			return false;
		return original;
	}
}
