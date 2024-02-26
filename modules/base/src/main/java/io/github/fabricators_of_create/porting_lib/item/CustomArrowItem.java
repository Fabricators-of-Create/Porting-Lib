package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.entity.projectile.AbstractArrow;

// TODO: Implement on Illusioner and AbstractSkeleton
public interface CustomArrowItem {
	default AbstractArrow customArrow(AbstractArrow arrow) {
		return arrow;
	}
}
