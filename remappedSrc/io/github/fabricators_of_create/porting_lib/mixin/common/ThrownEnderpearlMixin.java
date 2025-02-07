package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.EntityEvents.Teleport.EntityTeleportEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public abstract class ThrownEnderpearlMixin extends ThrownItemEntity {
	public ThrownEnderpearlMixin(EntityType<? extends ThrownItemEntity> entityType, World level) {
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
