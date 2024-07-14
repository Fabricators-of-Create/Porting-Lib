package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.HitResult;

@Mixin(AbstractHurtingProjectile.class)
public class AbstractHurtingProjectileMixin {
	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractHurtingProjectile;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
	private boolean onProjectileImpact(AbstractHurtingProjectile projectile, HitResult result) {
		return !EntityHooks.onProjectileImpact(projectile, result);
	}
}
