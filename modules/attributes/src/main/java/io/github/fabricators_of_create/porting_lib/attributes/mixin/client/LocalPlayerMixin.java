package io.github.fabricators_of_create.porting_lib.attributes.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.attributes.injections.PlayerAttributesInjection;
import net.minecraft.client.player.LocalPlayer;

import net.minecraft.world.entity.player.Abilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements PlayerAttributesInjection {
	@WrapOperation(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;mayfly:Z"))
	private boolean mayFly1(Abilities instance, Operation<Boolean> original) {
		return port_lib$mayFly(instance, original);
	}

	@WrapOperation(method = "hasEnoughFoodToStartSprinting", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;mayfly:Z"))
	private boolean mayFly2(Abilities instance, Operation<Boolean> original) {
		return port_lib$mayFly(instance, original);
	}
}
