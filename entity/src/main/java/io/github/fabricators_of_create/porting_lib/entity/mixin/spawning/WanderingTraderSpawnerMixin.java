package io.github.fabricators_of_create.porting_lib.entity.mixin.spawning;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.level.CustomSpawner;

@Mixin(WanderingTraderSpawner.class)
public abstract class WanderingTraderSpawnerMixin implements CustomSpawner {
	@WrapOperation(
			method = "spawn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/EntityType;spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;)Lnet/minecraft/world/entity/Entity;"
			)
	)
	private Entity fireSpawnEventForTrader(EntityType<WanderingTrader> type, ServerLevel level, BlockPos pos,
										   MobSpawnType reason, Operation<WanderingTrader> original) {
		WanderingTrader trader = type.create(level);
		boolean allowed = LivingEntityEvents.NATURAL_SPAWN.invoker().canSpawnMob(
				trader, pos.getX(), pos.getY(), pos.getZ(), level, this, reason
		).orElse(true);
		if (!allowed)
			return null;
		return original.call(type, level, pos, reason);
	}

	@WrapOperation(
			method = "tryToSpawnLlamaFor",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/EntityType;spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;)Lnet/minecraft/world/entity/Entity;"
			)
	)
	private Entity fireSpawnEventForLlama(EntityType<TraderLlama> type, ServerLevel level, BlockPos pos,
												MobSpawnType reason, Operation<TraderLlama> original) {
		TraderLlama llama = type.create(level);
		boolean allowed = LivingEntityEvents.NATURAL_SPAWN.invoker().canSpawnMob(
				llama, pos.getX(), pos.getY(), pos.getZ(), level, this, reason
		).orElse(true);
		if (!allowed)
			return null;
		return original.call(type, level, pos, reason);
	}
}
