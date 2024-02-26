package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.entity.events.ProjectileImpactEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Entity {
	public AbstractArrowMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;onHit(Lnet/minecraft/world/phys/HitResult;)V"))
	private boolean onImpact(AbstractArrow arrow, HitResult result, @Share("event") LocalRef<ProjectileImpactEvent> eventRef, @Share("state") LocalBooleanRef hasImpulseState) {
		ProjectileImpactEvent event = new ProjectileImpactEvent(arrow, result);
		event.sendEvent();
		eventRef.set(event);
		hasImpulseState.set(this.hasImpulse);
		return !event.isCanceled();
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;getPierceLevel()B"))
	private byte shouldBreak(AbstractArrow instance, Operation<Byte> original, @Share("event") LocalRef<ProjectileImpactEvent> eventRef, @Share("state") LocalBooleanRef hasImpulseState) {
		if (eventRef.get().isCanceled()) {
			this.hasImpulse = hasImpulseState.get();
			return 0;
		}
		return original.call(instance);
	}
}
