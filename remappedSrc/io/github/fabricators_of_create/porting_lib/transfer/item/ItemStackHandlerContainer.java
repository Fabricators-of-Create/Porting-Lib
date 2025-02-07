package io.github.fabricators_of_create.porting_lib.transfer.item;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.Set;

/**
 * An {@link ItemStackHandler} that is also a {@link Inventory}.
 */
public class ItemStackHandlerContainer extends ItemStackHandler implements Inventory {

	public ItemStackHandlerContainer() {
		super(1);
	}

	public ItemStackHandlerContainer(int stacks) {
		super(stacks);
	}

	public ItemStackHandlerContainer(ItemStack[] stacks) {
		super(stacks);
	}

	@Override
	public int size() {
		return getSlots();
	}

	@Override
	public boolean isEmpty() {
		return nonEmptyViews.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		if (indexInvalid(slot))
			return ItemStack.EMPTY;
		return getStackInSlot(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		if (indexInvalid(slot))
			return ItemStack.EMPTY;
		ItemStack stack = getStackInSlot(slot);
		return stack.split(amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		if (indexInvalid(slot))
			return ItemStack.EMPTY;
		ItemStack stack = getStackInSlot(slot);
		setStackInSlot(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (indexInvalid(slot))
			return;
		setStackInSlot(slot, stack);
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	public boolean isValid(int index, ItemStack stack) {
		if (indexInvalid(index))
			return false;
		return isItemValid(index, ItemVariant.of(stack));
	}

	@Override
	public int count(Item item) {
		int total = 0;
		IntSortedSet indices = getIndices(item);
		for (IntIterator itr = indices.intIterator(); itr.hasNext();) {
			int i = itr.nextInt();
			ItemStack stack = getStackInSlot(i);
			total += stack.getCount();
		}
		return total;
	}

	@Override
	public boolean containsAny(Set<Item> set) {
		for (Item item : set) {
			if (!getIndices(item).isEmpty())
				return true;
		}
		return false;
	}

	@Override
	public void clear() {
		for (int i = 0; i < getSlots(); i++) {
			setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	public boolean indexInvalid(int slot) {
		return slot < 0 || slot >= getSlots();
	}
}
