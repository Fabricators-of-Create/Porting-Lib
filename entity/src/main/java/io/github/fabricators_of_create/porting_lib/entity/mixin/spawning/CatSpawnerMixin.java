package io.github.fabricators_of_create.porting_lib.entity.mixin.spawning;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.level.CustomSpawner;

@Mixin(CatSpawner.class)
public abstract class CatSpawnerMixin implements CustomSpawner {
	@ModifyExpressionValue(
			method = "spawnCat",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"
			)
	)
	private Entity fireSpawnEvent(Entity cat, BlockPos pos, ServerLevel level) {
		return LivingEntityEvents.NATURAL_SPAWN.invoker().canSpawnMob(
				(Cat) cat, pos.getX(), pos.getY(), pos.getZ(), level, this, MobSpawnType.NATURAL
		).orElse(true) ? cat : null;
	}
}
