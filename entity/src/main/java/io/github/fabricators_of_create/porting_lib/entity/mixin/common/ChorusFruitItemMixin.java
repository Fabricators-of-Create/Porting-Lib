package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ChorusFruitItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChorusFruitItem.class)
public abstract class ChorusFruitItemMixin {
	@WrapOperation(
			method = "finishUsingItem",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;randomTeleport(DDDZ)Z")
	)
	private boolean port_lib$finishUsingItem(LivingEntity instance, double x, double y, double z, boolean particleEffects, Operation<Boolean> original) {
		EntityEvents.Teleport.EntityTeleportEvent event = new EntityEvents.Teleport.EntityTeleportEvent(instance, x, y, z);
		event.sendEvent();
		if (event.isCanceled()) {
			return false;
		} else {
			return original.call(instance, x, y, z, particleEffects);
		}
	}
}
