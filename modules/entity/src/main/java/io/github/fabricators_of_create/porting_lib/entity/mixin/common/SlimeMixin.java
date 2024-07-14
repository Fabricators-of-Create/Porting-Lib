package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.ext.SlimeExt;

import net.minecraft.world.entity.EntityDimensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.monster.Slime;

@Mixin(Slime.class)
public class SlimeMixin implements SlimeExt {
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityDimensions;width()F"))
	private float handleParticles(EntityDimensions instance, Operation<Float> original) {
		if (!spawnCustomParticles())
			return original.call(instance);
		return 0; // Return 0 to prevent adding particles
	}
}
