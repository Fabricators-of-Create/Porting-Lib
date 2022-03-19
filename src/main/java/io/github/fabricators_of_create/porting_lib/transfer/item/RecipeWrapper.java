package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

/**
 * Wraps a ItemStackHandler in a Container for use in recipes and crafting.
 */
public class RecipeWrapper implements Container {
	protected final ItemStackHandler handler;

	public RecipeWrapper(ItemStackHandler handler) {
		this.handler = handler;
	}

	@Override
	public int getContainerSize() {
		return handler.stacks.length;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : handler.stacks) {
			if (!stack.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int index) {
		return handler.getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack[] stacks = handler.stacks;
		if (index > 0 && index < stacks.length) {
			ItemStack current = stacks[index];
			stacks[index] = ItemStack.EMPTY;
			return current.split(count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return removeItem(index, Integer.MAX_VALUE);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		ItemStack[] stacks = handler.stacks;
		if (index > 0 && index < stacks.length) {
			stacks[index] = stack;
		}
	}

	@Override
	public void clearContent() {
		Arrays.fill(handler.stacks, ItemStack.EMPTY);
	}

	@Override
	public int getMaxStackSize() { return 0; }
	@Override
	public void setChanged() {}
	@Override
	public boolean stillValid(Player player) { return false; }
	@Override
	public void startOpen(Player player) {}
	@Override
	public void stopOpen(Player player) {}
}
