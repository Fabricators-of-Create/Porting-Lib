package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.item.CustomArrowItem;
import io.github.fabricators_of_create.porting_lib.item.InfiniteArrowItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProjectileWeaponItem.class)
public class ProjectileWeaponItemMixin {
	@ModifyExpressionValue(method = "useAmmo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasInfiniteMaterials()Z"))
	private static boolean checkInfiniteAmmo(boolean original, ItemStack p_331207_, ItemStack p_331434_, LivingEntity p_330302_, boolean p_330934_) {
		return (original || (p_331434_.getItem() instanceof InfiniteArrowItem ai && ai.isInfinite(p_331434_, p_331207_, p_330302_)));
	}

	@ModifyReturnValue(method = "createProjectile", at = @At("RETURN"))
	private Projectile customArrow(Projectile original, Level level, LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2) {
		if (this instanceof CustomArrowItem arrowItem && original instanceof AbstractArrow arrow)
			return arrowItem.customArrow(arrow, itemStack, itemStack2);
		return original;
	}
}
