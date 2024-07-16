package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.CustomArrowItem;
import net.minecraft.world.entity.monster.AbstractSkeleton;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractSkeleton.class)
public class AbstractSkeletonMixin {
	@ModifyVariable(method = "performRangedAttack", at = @At(value = "STORE", ordinal = 0), index = 5)
	private AbstractArrow modifyCustomArrow(AbstractArrow value, @Local(index = 3) ItemStack weaponStack, @Local(index = 4) ItemStack projectileStack) {
		if (weaponStack.getItem() instanceof CustomArrowItem bowItem)
			return bowItem.customArrow(value, projectileStack, weaponStack);
		return value;
	}
}
