package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.EntityTeleportEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile {
	public ThrownEnderpearlMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "onHit", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", ordinal = 0), cancellable = true)
	private void port_lib$onHit(HitResult result, CallbackInfo ci) {
		EntityTeleportEvent event = new EntityTeleportEvent(getOwner(), getX(), getY(), getZ());
		event.sendEvent();
		if (event.isCanceled()) {
			discard();
			ci.cancel();
		}
	}
}
