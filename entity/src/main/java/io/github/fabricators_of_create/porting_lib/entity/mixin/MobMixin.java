package io.github.fabricators_of_create.porting_lib.entity.mixin;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public abstract class MobMixin {
	@Inject(method = "setTarget", at = @At("TAIL"))
	private void onSetTarget(@Nullable LivingEntity target, CallbackInfo ci) {
		LivingEntityEvents.SET_TARGET.invoker().onMobEntitySetTarget((Mob) (Object) this, target);
	}
}
