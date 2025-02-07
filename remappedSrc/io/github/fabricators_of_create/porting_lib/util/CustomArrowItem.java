package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.entity.projectile.PersistentProjectileEntity;

// TODO: Implement on Illusioner and AbstractSkeleton
public interface CustomArrowItem {
	default PersistentProjectileEntity customArrow(PersistentProjectileEntity arrow) {
		return arrow;
	}
}
