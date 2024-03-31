package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents.ChangeTarget.ChangeTargetEvent;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents.ChangeTarget.ChangeTargetEvent.LivingTargetType;
import io.github.fabricators_of_create.porting_lib.event.common.MobEntitySetTargetCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {
	@Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
	private void port_lib$onChangeTarget(LivingEntity target, CallbackInfo ci, @Share("changeTargetEvent") LocalRef<ChangeTargetEvent> changeTargetEvent) {
		changeTargetEvent.set(new LivingEntityEvents.ChangeTarget.ChangeTargetEvent((Mob) (Object) this, target, LivingTargetType.MOB_TARGET));
		changeTargetEvent.get().sendEvent();
		if (changeTargetEvent.get().isCanceled())
			ci.cancel();
	}

	@WrapOperation(method = "setTarget", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Mob;target:Lnet/minecraft/world/entity/LivingEntity;"))
	private void port_lib$wrapSetTarget(Mob instance, LivingEntity value, Operation<Void> original, @Share("changeTargetEvent") LocalRef<ChangeTargetEvent> changeTargetEvent) {
		original.call(instance, changeTargetEvent.get().getNewTarget());
	}

	@Inject(method = "setTarget", at = @At("TAIL"))
	private void port_lib$setTarget(LivingEntity target, CallbackInfo ci) {
		MobEntitySetTargetCallback.EVENT.invoker().onMobEntitySetTarget((Mob) (Object) this, target);
	}
}
