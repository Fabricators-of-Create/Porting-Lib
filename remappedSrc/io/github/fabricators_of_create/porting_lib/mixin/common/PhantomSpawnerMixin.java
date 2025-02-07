package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.gen.PhantomSpawner;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Phantom;moveTo(Lnet/minecraft/core/BlockPos;FF)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void port_lib$checkSpawn(ServerWorld serverLevel, boolean bl, boolean bl2, CallbackInfoReturnable<Integer> cir, Random random, int i, Iterator var6, PlayerEntity player, BlockPos blockPos, LocalDifficulty difficultyInstance, ServerStatHandler serverStatsCounter, int j, int k, BlockPos blockPos2, BlockState blockState, FluidState fluidState, EntityData spawnGroupData, int l, int m, PhantomEntity phantom) {
		if (LivingEntityEvents.CHECK_SPAWN.invoker().onCheckSpawn(phantom, serverLevel, blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), null, SpawnReason.NATURAL))
			cir.setReturnValue(0);
	}
}
