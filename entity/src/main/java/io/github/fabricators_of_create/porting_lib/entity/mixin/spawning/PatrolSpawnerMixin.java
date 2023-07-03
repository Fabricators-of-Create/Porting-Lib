package io.github.fabricators_of_create.porting_lib.entity.mixin.spawning;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.levelgen.PatrolSpawner;

@Mixin(PatrolSpawner.class)
public abstract class PatrolSpawnerMixin implements CustomSpawner {
	@Inject(
			method = "spawnPatrolMember",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/PatrollingMonster;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;"
			),
			cancellable = true
	)
	private void fireSpawnEvent(ServerLevel world, BlockPos pos, RandomSource random, boolean captain,
									CallbackInfoReturnable<Boolean> cir, @Local PatrollingMonster mob) {
		boolean allowed = LivingEntityEvents.NATURAL_SPAWN.invoker().canSpawnMob(
				mob, mob.getX(), mob.getY(), mob.getZ(), world, this, MobSpawnType.PATROL
		).orElse(true);
		if (!allowed)
			cir.setReturnValue(false);
	}
}
