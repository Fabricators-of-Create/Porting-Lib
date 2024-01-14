package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PatrolSpawner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PatrolSpawner.class)
public class PatrolSpawnerMixin {
	@Inject(method = "spawnPatrolMember", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/PatrollingMonster;setPos(DDD)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void port_lib$checkSpawn(ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, boolean bl, CallbackInfoReturnable<Boolean> cir, BlockState blockState, PatrollingMonster patrollingMonster) {
		if (LivingEntityEvents.CHECK_SPAWN.invoker().onCheckSpawn(patrollingMonster, serverLevel, blockPos.getX(), blockPos.getY(), blockPos.getZ(), null, MobSpawnType.PATROL))
			cir.setReturnValue(false);
	}
}
