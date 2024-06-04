package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents.ChangeTarget.ChangeTargetEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents.ChangeTarget.ChangeTargetEvent.LivingTargetType;
import io.github.fabricators_of_create.porting_lib.entity.events.MobEntitySetTargetCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public abstract class MobMixin {
	@ModifyVariable(method = "setTarget", at = @At("HEAD"), argsOnly = true)
	private LivingEntity port_lib$onChangeTarget(LivingEntity value) {
		ChangeTargetEvent changeTargetEvent = new ChangeTargetEvent((Mob) (Object) this, value, LivingTargetType.MOB_TARGET);
		changeTargetEvent.sendEvent();
		if (changeTargetEvent.isCanceled())
			return null;
		return changeTargetEvent.getNewTarget();
	}

	@Inject(method = "setTarget", at = @At("TAIL"))
	private void port_lib$setTarget(LivingEntity target, CallbackInfo ci) {
		MobEntitySetTargetCallback.EVENT.invoker().onMobEntitySetTarget((Mob) (Object) this, target);
	}
}
