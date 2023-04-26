package io.github.fabricators_of_create.porting_lib.mixin.common;

import net.minecraft.server.level.WorldGenRegion;

import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.Mob;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldGenRegion.class)
public class WorldGenRegionMixin {
	@Inject(method = "addFreshEntity", at = @At("HEAD"), cancellable = true)
	private void isSpawnCanceled(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof Mob mob && mob.isSpawnCancelled()) cir.setReturnValue(false);
	}
}
