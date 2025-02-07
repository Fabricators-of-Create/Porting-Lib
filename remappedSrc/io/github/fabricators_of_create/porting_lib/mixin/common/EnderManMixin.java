package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.EntityEvents.Teleport.EntityTeleportEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public abstract class EnderManMixin extends HostileEntity {
	protected EnderManMixin(EntityType<? extends HostileEntity> entityType, World level) {
		super(entityType, level);
	}

	@Inject(method = "teleport(DDD)Z", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/EnderMan;randomTeleport(DDDZ)Z"))
	private void port_lib$teleport(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
		EntityTeleportEvent event = new EntityTeleportEvent(this, x, y, z);
		event.sendEvent();
		if (event.isCanceled())
			cir.setReturnValue(false);
	}
}
