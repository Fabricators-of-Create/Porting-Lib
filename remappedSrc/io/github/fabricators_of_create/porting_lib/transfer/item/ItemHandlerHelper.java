package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.minecraft.item.ItemStack;

public class ItemHandlerHelper {
	public static boolean canItemStacksStack(ItemStack first, ItemStack second) {
		if (first.isEmpty() || !first.isItemEqualIgnoreDamage(second) || first.hasNbt() != second.hasNbt()) return false;

		return !first.hasNbt() || first.getNbt().equals(second.getNbt());
	}

	public static ItemStack copyStackWithSize(ItemStack stack, int size) {
		if (size == 0) return ItemStack.EMPTY;
		ItemStack copy = stack.copy();
		copy.setCount(size);
		return copy;
	}

	public static ItemStack growCopy(ItemStack stack, int amount) {
		return copyStackWithSize(stack, stack.getCount() + amount);
	}
}
