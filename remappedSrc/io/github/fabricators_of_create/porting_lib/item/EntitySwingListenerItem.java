package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface EntitySwingListenerItem {
	boolean onEntitySwing(ItemStack stack, LivingEntity entity);
}
