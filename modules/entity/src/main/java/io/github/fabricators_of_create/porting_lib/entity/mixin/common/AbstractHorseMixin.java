package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public class AbstractHorseMixin {
	@Inject(method = "executeRidersJump", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;hasImpulse:Z", shift = At.Shift.AFTER))
	private void onHorseJump(float f, Vec3 vec3, CallbackInfo ci) {
		new LivingEntityEvents.LivingJumpEvent((LivingEntity) (Object) this).sendEvent();
	}
}
