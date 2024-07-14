package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.phys.HitResult;

@Mixin(LlamaSpit.class)
public class LlamaSpitMixin {
	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/LlamaSpit;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
	private boolean onProjectileImpact(LlamaSpit projectile, HitResult result) {
		return result.getType() != HitResult.Type.MISS && !EntityHooks.onProjectileImpact(projectile, result);
	}
}
