package io.github.fabricators_of_create.porting_lib.mixin.common;


import io.github.fabricators_of_create.porting_lib.event.common.ExplosionEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
	@Inject(
			method = "explode",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Explosion;explode()V",
					shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	@SuppressWarnings("InvalidInjectorMethodSignature")
	public void port_lib$onStartExplosion(Entity exploder, DamageSource damageSource,
										ExplosionDamageCalculator context, double x,
										double y, double z, float size, boolean causesFire,
										Explosion.BlockInteraction mode, CallbackInfoReturnable<Explosion> cir,
										Explosion explosion) {
		if(ExplosionEvents.START.invoker().onExplosionStart((Level) (Object) this, explosion))
			cir.setReturnValue(explosion);
	}
}
