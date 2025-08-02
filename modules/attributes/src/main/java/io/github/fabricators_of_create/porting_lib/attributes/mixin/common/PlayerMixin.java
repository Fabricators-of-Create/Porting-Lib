package io.github.fabricators_of_create.porting_lib.attributes.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import io.github.fabricators_of_create.porting_lib.attributes.injections.PlayerAttributesInjection;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin implements PlayerAttributesInjection {
	@ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
	private static AttributeSupplier.Builder addPlayerAttributes(AttributeSupplier.Builder original) {
		return original.add(PortingLibAttributes.CREATIVE_FLIGHT);
	}

	@WrapOperation(method = "causeFallDamage", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;mayfly:Z"))
	private boolean mayFly(Abilities instance, Operation<Boolean> original) {
		return port_lib$mayFly(instance, original);
	}
}
