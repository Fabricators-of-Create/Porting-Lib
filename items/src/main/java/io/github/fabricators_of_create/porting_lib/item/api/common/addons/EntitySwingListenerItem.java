package io.github.fabricators_of_create.porting_lib.item.api.common.addons;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface EntitySwingListenerItem {
	boolean onEntitySwing(ItemStack stack, LivingEntity entity);
}
