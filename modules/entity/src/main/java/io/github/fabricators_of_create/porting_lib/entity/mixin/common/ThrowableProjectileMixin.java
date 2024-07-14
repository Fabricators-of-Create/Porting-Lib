package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.minecraft.world.entity.projectile.ThrowableProjectile;

import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrowableProjectile.class)
public class ThrowableProjectileMixin {
	@WrapWithCondition(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"
			)
	)
	private boolean onProjectileImpact(ThrowableProjectile projectile, HitResult result) {
		return !EntityHooks.onProjectileImpact(projectile, result);
	}
}
