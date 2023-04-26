package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.event.common.MobSpawnEvents;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BaseSpawner;

@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin {
	@ModifyExpressionValue(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;contains(Ljava/lang/String;I)Z"))
	private boolean onFinalizeSpawnSpawner(boolean original, ServerLevel pServerLevel, BlockPos pPos, @Local(index = 7) CompoundTag compoundtag, @Local(index = 18) Entity entity, @Share("event_spawn")LocalRef<MobSpawnEvents.FinalizeSpawn> ref) {
		// Forge: Patch in the spawn event for spawners so it may be fired unconditionally, instead of only when vanilla normally would trigger it.
		var event = PortingHooks.onFinalizeSpawnSpawner((Mob) entity, pServerLevel, pServerLevel.getCurrentDifficultyAt(entity.blockPosition()), null, compoundtag, (BaseSpawner) (Object) this);
		ref.set(event);
		return original && event != null;
	}

	@ModifyArgs(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;"))
	private void modifyFinalizeSpawnArgs(Args args, @Share("event_spawn")LocalRef<MobSpawnEvents.FinalizeSpawn> ref) {
		var event = ref.get();
		args.set(1, event.getDifficulty());
		args.set(2, event.getSpawnType());
		args.set(3, event.getSpawnData());
		args.set(4, event.getSpawnTag());
	}
}
