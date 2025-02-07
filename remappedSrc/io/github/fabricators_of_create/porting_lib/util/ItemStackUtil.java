package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemStackUtil {
	public static boolean equals(ItemStack stack1, ItemStack stack2, boolean limitTags) {
		if (stack1.isEmpty()) {
			return stack2.isEmpty();
		} else {
			return !stack2.isEmpty() && stack1.getCount() == stack2.getCount() && stack1.getItem() == stack2.getItem() &&
					(limitTags ? areTagsEqual(stack1, stack2) : ItemStack.areNbtEqual(stack1, stack2));
		}
	}

	public static boolean areTagsEqual(ItemStack stack1, ItemStack stack2) {
		NbtCompound tag1 = stack1.getNbt();
		NbtCompound tag2 = stack2.getNbt();
		if (tag1 == null) {
			return tag2 == null;
		} else {
			return tag1.equals(tag2);
		}
	}

	public static boolean canItemStacksStack(ItemStack first, ItemStack second) {
		if (first.isEmpty() || !first.isItemEqualIgnoreDamage(second) || first.hasNbt() != second.hasNbt()) return false;

		return !first.hasNbt() || first.getNbt().equals(second.getNbt());
	}
}
