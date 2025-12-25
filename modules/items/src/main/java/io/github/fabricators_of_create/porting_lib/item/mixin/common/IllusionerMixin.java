package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.CustomArrowItem;

import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Illusioner.class)
public class IllusionerMixin {
	@ModifyExpressionValue(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getMobArrow(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;FLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;"))
	private AbstractArrow modifyCustomArrow(AbstractArrow value, @Local(ordinal = 0) ItemStack weaponStack, @Local(ordinal = 1) ItemStack projectileStack) {
		if (weaponStack.getItem() instanceof CustomArrowItem bowItem)
			return bowItem.customArrow(value, projectileStack, weaponStack);
		return value;
	}
}
