package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.MobEffectEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Spider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Spider.class)
public class SpiderMixin {
	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	private void isEffectApplicable(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> cir) {
		if (effectInstance.getEffect() == MobEffects.POISON) {
			MobEffectEvent.Applicable event = new MobEffectEvent.Applicable((LivingEntity) (Object) this, effectInstance);
			event.sendEvent();
			if (event.getResult() != BaseEvent.Result.DEFAULT)
				cir.setReturnValue(event.getResult() == BaseEvent.Result.ALLOW);
		}
	}
}
