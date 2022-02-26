package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;

public class BurnUtil {
	public static int getBurnTime(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else {
			Integer burnTime = FuelRegistry.INSTANCE.get(stack.getItem());
			return burnTime == null ? 0 : burnTime;
		}
	}
}
