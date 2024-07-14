package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;

import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;

import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Entity {
	public AbstractArrowMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	@WrapOperation(method = "tick", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"
	))
	private ProjectileDeflection onProjectileImpact(AbstractArrow instance, HitResult hitResult, Operation<ProjectileDeflection> original, @Share("canceled") LocalBooleanRef canceled, @Share("hasImpulse") LocalBooleanRef lastHasImpulse) {
		if (EntityHooks.onProjectileImpact(instance, hitResult)) {
			canceled.set(true);
			lastHasImpulse.set(this.hasImpulse);
			return ProjectileDeflection.REVERSE; // Return anything that isn't none
		}
		canceled.set(false);
		return original.call(instance, hitResult);
	}

	@Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;hasImpulse:Z", shift = At.Shift.AFTER))
	private void restoreHasImpulse(CallbackInfo ci, @Share("canceled") LocalBooleanRef canceled, @Share("hasImpulse") LocalBooleanRef lastHasImpulse) {
		if (canceled.get())
			this.hasImpulse = lastHasImpulse.get();
	}
}
