package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.events.ProjectileImpactEvent;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.ThrowableProjectile;

import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrowableProjectile.class)
public class ThrowableProjectileMixin {
	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;onHit(Lnet/minecraft/world/phys/HitResult;)V"))
	private boolean onImpact(ThrowableProjectile projectile, HitResult result) {
		ProjectileImpactEvent event = new ProjectileImpactEvent(projectile, result);
		event.sendEvent();
		return !event.isCanceled();
	}
}
