package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.events.ProjectileImpactEvent;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.phys.HitResult;

@Mixin(ShulkerBullet.class)
public class ShulkerBulletMixin {
	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;onHit(Lnet/minecraft/world/phys/HitResult;)V"))
	private boolean onImpact(ShulkerBullet projectile, HitResult result) {
		ProjectileImpactEvent event = new ProjectileImpactEvent(projectile, result);
		event.sendEvent();
		return !event.isCanceled();
	}
}
