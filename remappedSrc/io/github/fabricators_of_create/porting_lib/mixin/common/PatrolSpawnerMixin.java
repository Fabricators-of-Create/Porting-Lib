package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.PillagerSpawner;

@Mixin(PillagerSpawner.class)
public class PatrolSpawnerMixin {
	@Inject(method = "spawnPatrolMember", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/PatrollingMonster;setPos(DDD)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void port_lib$checkSpawn(ServerWorld serverLevel, BlockPos blockPos, Random random, boolean bl, CallbackInfoReturnable<Boolean> cir, BlockState blockState, PatrolEntity patrollingMonster) {
		if (LivingEntityEvents.CHECK_SPAWN.invoker().onCheckSpawn(patrollingMonster, serverLevel, blockPos.getX(), blockPos.getY(), blockPos.getZ(), null, SpawnReason.PATROL))
			cir.setReturnValue(false);
	}
}
