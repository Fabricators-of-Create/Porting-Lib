package io.github.fabricators_of_create.porting_lib.tags;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class DyeColorUtil {
	@Nullable
	public static DyeColor getColor(ItemStack stack) {
		if (stack.getItem() instanceof DyeItem)
			return ((DyeItem)stack.getItem()).getDyeColor();

		for (int x = 0; x < DyeColor.BLACK.getId(); x++) {
			DyeColor color = DyeColor.byId(x);
			if (stack.is(color.port_lib$getTag()))
				return color;
		}

		return null;
	}
}
