package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import io.github.fabricators_of_create.porting_lib.attributes.extensions.PlayerAttributesExtensions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin implements PlayerAttributesExtensions {
	@ModifyConstant(method = "attack", constant = @Constant(doubleValue = 9.0))
	private double modifyAttackRange(final double attackRange) {
		return Mth.square(this.getEntityReach());
	}

	@Inject(method = "createAttributes", at = @At("RETURN"))
	private static void port_lib$addKnockback(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
		cir.getReturnValue().add(PortingLibAttributes.BLOCK_REACH).add(PortingLibAttributes.ENTITY_REACH);
	}
}
