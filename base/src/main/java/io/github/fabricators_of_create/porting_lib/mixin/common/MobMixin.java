package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents.ChangeTarget.ChangeTargetEvent;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents.ChangeTarget.ChangeTargetEvent.LivingTargetType;
import io.github.fabricators_of_create.porting_lib.event.common.MobEntitySetTargetCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public abstract class MobMixin {
	@Unique private ChangeTargetEvent port_lib$changeTargetEvent;

	@Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
	private void port_lib$onChangeTarget(LivingEntity target, CallbackInfo ci) {
		port_lib$changeTargetEvent = new LivingEntityEvents.ChangeTarget.ChangeTargetEvent((Mob) (Object) this, target, LivingTargetType.MOB_TARGET);
		port_lib$changeTargetEvent.sendEvent();
		if (port_lib$changeTargetEvent.isCanceled())
			ci.cancel();
	}

	@WrapOperation(method = "setTarget", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Mob;target:Lnet/minecraft/world/entity/LivingEntity;"))
	private void port_lib$wrapSetTarget(Mob instance, LivingEntity value, Operation<Void> original) {
		original.call(instance, port_lib$changeTargetEvent.getNewTarget());
	}

	@Inject(method = "setTarget", at = @At("TAIL"))
	private void port_lib$setTarget(LivingEntity target, CallbackInfo ci) {
		MobEntitySetTargetCallback.EVENT.invoker().onMobEntitySetTarget((Mob) (Object) this, target);
	}
}
