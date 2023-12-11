package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.events.ProjectileImpactEvent;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.HitResult;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;onHit(Lnet/minecraft/world/phys/HitResult;)V"))
	private boolean onImpact(FireworkRocketEntity projectile, HitResult result) {
		if (result.getType() == HitResult.Type.MISS)
			return true;
		ProjectileImpactEvent event = new ProjectileImpactEvent(projectile, result);
		event.sendEvent();
		return !event.isCanceled();
	}
}
