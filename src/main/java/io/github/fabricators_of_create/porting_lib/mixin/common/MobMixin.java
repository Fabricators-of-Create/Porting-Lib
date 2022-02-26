package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.event.MobEntitySetTargetCallback;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public abstract class MobMixin {
	@Inject(method = "setTarget", at = @At("TAIL"))
	private void port_lib$setTarget(LivingEntity target, CallbackInfo ci) {
		MobEntitySetTargetCallback.EVENT.invoker().onMobEntitySetTarget((Mob) (Object) this, target);
	}
}
