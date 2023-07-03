package io.github.fabricators_of_create.porting_lib.entity.mixin.spawning;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.phys.Vec3;

@Mixin(VillageSiege.class)
public abstract class VillageSiegeMixin implements CustomSpawner {
	@Inject(
			method = "trySpawn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/Zombie;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;"
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private void fireSpawnEvent(ServerLevel level, CallbackInfo ci, Vec3 pos, Zombie zombie) {
		boolean cancelled = LivingEntityEvents.NATURAL_SPAWN.invoker().canSpawnMob(
				zombie, pos.x, pos.y, pos.z, level, this, MobSpawnType.EVENT
		) == TriState.FALSE;
		if (cancelled)
			ci.cancel();
	}
}
