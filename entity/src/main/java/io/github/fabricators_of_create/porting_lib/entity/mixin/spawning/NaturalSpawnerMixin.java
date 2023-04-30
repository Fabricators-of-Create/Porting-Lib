package io.github.fabricators_of_create.porting_lib.entity.mixin.spawning;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.entity.events.living.NaturalMobSpawnCallback;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
	@WrapOperation(
			method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/NaturalSpawner;isValidPositionForMob(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;D)Z"
			)
	)
	private static boolean fireSpawnEvent(ServerLevel level, Mob mob, double distSqr, Operation<Boolean> original) {
		return NaturalMobSpawnCallback.EVENT.invoker().canSpawnMob(
				mob, mob.getX(), mob.getY(), mob.getZ(), level, null, MobSpawnType.NATURAL
		).orElseGet(() -> original.call(level, mob, distSqr));
	}

	@WrapOperation(
			method = "spawnMobsForChunkGeneration",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Mob;checkSpawnRules(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/MobSpawnType;)Z"
			)
	)
	private static boolean fireSpawnEventForChunkGen(Mob mob, LevelAccessor level, MobSpawnType type, Operation<Boolean> original,
													 @Share("NaturalMobSpawnCallback") LocalRef<TriState> sharedResult) {
		TriState result = NaturalMobSpawnCallback.EVENT.invoker().canSpawnMob(
				mob, mob.getX(), mob.getY(), mob.getZ(), level, null, type
		);
		sharedResult.set(result);
		return result.orElseGet(() -> original.call(mob, level, type));
	}

	@WrapOperation(
			method = "spawnMobsForChunkGeneration",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Mob;checkSpawnObstruction(Lnet/minecraft/world/level/LevelReader;)Z"
			)
	)
	private static boolean checkEventResultForObstruction(Mob mob, LevelReader level, Operation<Boolean> original,
														  @Share("NaturalMobSpawnCallback") LocalRef<TriState> sharedResult) {
		TriState result = sharedResult.get();
		// must be true or default + spawn rules success, otherwise early exit
		if (result == TriState.TRUE)
			return true; // forced success
		// default, fallback
		return original.call(mob, level);
	}
}
