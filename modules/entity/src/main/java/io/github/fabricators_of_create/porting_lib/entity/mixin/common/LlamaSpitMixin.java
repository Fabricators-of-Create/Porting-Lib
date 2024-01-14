package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.events.ProjectileImpactEvent;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.phys.HitResult;

@Mixin(LlamaSpit.class)
public class LlamaSpitMixin {
	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/LlamaSpit;onHit(Lnet/minecraft/world/phys/HitResult;)V"))
	private boolean onImpact(LlamaSpit projectile, HitResult result) {
		if (result.getType() == HitResult.Type.MISS)
			return true; // NeoForge prevents any misses here, however vanilla doesn't so we keep vanilla behavior
		ProjectileImpactEvent event = new ProjectileImpactEvent(projectile, result);
		event.sendEvent();
		return !event.isCanceled();
	}
}
