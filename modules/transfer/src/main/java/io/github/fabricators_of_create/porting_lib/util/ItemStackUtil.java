package io.github.fabricators_of_create.porting_lib.util;

import java.util.Arrays;

import net.minecraft.world.item.ItemStack;

public class ItemStackUtil {
	public static ItemStack[] createEmptyStackArray(int size) {
		ItemStack[] stacks = new ItemStack[size];
		Arrays.fill(stacks, ItemStack.EMPTY);
		return stacks;
	}
}
