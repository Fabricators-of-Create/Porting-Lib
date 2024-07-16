package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

public interface CustomArrowItem {
	default AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
		return arrow;
	}
}
