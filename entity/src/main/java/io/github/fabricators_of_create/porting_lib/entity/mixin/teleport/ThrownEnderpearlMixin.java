package io.github.fabricators_of_create.porting_lib.entity.mixin.teleport;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents.EntityTeleportEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile {
	public ThrownEnderpearlMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(
			method = "onHit",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/RandomSource;nextFloat()F"
			),
			cancellable = true
	)
	private void fireTeleportEvent(HitResult result, CallbackInfo ci) {
		EntityTeleportEvent event = new EntityTeleportEvent(getOwner(), getX(), getY(), getZ());
		EntityMoveEvents.TELEPORT.invoker().onTeleport(event);
		if (event.isCancelled()) {
			discard();
			ci.cancel();
		}
	}
}
