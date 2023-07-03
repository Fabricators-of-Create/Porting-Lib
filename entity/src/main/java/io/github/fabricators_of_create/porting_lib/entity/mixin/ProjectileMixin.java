package io.github.fabricators_of_create.porting_lib.entity.mixin;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {
	@Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
	private void onProjectileHit(HitResult result, CallbackInfo ci) {
		if (EntityEvents.PROJECTILE_IMPACT.invoker().onImpact((Projectile) (Object) this, result))
			ci.cancel();
	}
}
