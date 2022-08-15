package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PhantomSpawner;

import net.minecraft.world.level.material.FluidState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Random;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Phantom;moveTo(Lnet/minecraft/core/BlockPos;FF)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void port_lib$checkSpawn(ServerLevel serverLevel, boolean bl, boolean bl2, CallbackInfoReturnable<Integer> cir, RandomSource randomSource, int i, Iterator var6, Player player, BlockPos blockPos, DifficultyInstance difficultyInstance, ServerStatsCounter serverStatsCounter, int j, int k, BlockPos blockPos2, BlockState blockState, FluidState fluidState, SpawnGroupData spawnGroupData, int l, int m, Phantom phantom) {
		if (LivingEntityEvents.CHECK_SPAWN.invoker().onCheckSpawn(phantom, serverLevel, blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), null, MobSpawnType.NATURAL))
			cir.setReturnValue(0);
	}
}
