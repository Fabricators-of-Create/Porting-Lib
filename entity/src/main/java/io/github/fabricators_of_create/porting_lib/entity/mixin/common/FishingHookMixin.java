package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.events.ProjectileImpactEvent;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.FishingHook;

import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingHook.class)
public class FishingHookMixin {
	@WrapWithCondition(method = "checkCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;onHit(Lnet/minecraft/world/phys/HitResult;)V"))
	private boolean onImpact(FishingHook projectile, HitResult result) {
		if (result.getType() == HitResult.Type.MISS)
			return true;
		ProjectileImpactEvent event = new ProjectileImpactEvent(projectile, result);
		event.sendEvent();
		return !event.isCanceled();
	}
}
