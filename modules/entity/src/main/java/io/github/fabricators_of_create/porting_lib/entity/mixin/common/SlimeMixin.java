package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.extensions.SlimeExtension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.monster.Slime;

@Mixin(Slime.class)
public class SlimeMixin implements SlimeExtension {
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Slime;getSize()I"))
	private int handleParticles(Slime slime, Operation<Integer> operation) {
		if (!spawnCustomParticles())
			return operation.call(slime);
		return 0; // Return 0 to prevent adding particles
	}
}
