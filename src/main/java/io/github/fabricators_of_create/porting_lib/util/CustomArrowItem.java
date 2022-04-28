package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.world.entity.projectile.AbstractArrow;

public interface CustomArrowItem {
	default AbstractArrow customArrow(AbstractArrow arrow) {
		return arrow;
	}
}
