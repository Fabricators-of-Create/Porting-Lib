package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.npc.CatSpawner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CatSpawner.class)
public class CatSpawnerMixin {
	@Inject(method = "spawnCat", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/animal/Cat;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;",
			shift = At.Shift.BEFORE),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	public void port_lib$checkSpawn(BlockPos blockPos, ServerLevel serverLevel, CallbackInfoReturnable<Integer> cir, Cat cat) {
		if (LivingEntityEvents.CHECK_SPAWN.invoker().onCheckSpawn(cat, serverLevel, blockPos.getX(), blockPos.getY(), blockPos.getZ(), null, MobSpawnType.NATURAL))
			cir.setReturnValue(0);
	}
}
