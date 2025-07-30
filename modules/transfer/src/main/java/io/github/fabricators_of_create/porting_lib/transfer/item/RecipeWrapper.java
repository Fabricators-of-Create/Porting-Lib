package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.crafting.RecipeInput;

public class RecipeWrapper implements RecipeInput {
	protected final SlottedStackStorage inv;

	public RecipeWrapper(SlottedStackStorage inv) {
		this.inv = inv;
	}

	/**
	 * Returns the size of this inventory.
	 */
	@Override
	public int size() {
		return inv.getSlotCount();
	}

	/**
	 * Returns the stack in this slot. This stack should be a modifiable reference, not a copy of a stack in your inventory.
	 */
	@Override
	public ItemStack getItem(int slot) {
		return inv.getStackInSlot(slot);
	}
}
