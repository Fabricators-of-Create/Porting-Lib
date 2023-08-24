package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

/**
 * An {@link ItemStackHandler} that is also a {@link Container}.
 */
public class ItemStackHandlerContainer extends ItemStackHandler implements Container {

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
	public int getContainerSize() {
		return getSlotCount();
	}

	@Override
	public boolean isEmpty() {
		return super.empty();
	}

	@Override
	@NotNull
	public ItemStack getItem(int slot) {
		if (indexInvalid(slot))
			return ItemStack.EMPTY;
		return getStackInSlot(slot);
	}

	@Override
	@NotNull
	public ItemStack removeItem(int index, int amount) {
		if (indexInvalid(index))
			return ItemStack.EMPTY;
		ItemStackHandlerSlot slot = getSlot(index);
		ItemStack stack = slot.getStack();
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		int count = stack.getCount();
		int toRemove = Math.min(amount, count);
		int remaining = count - toRemove;
		ItemStack removed = ItemHandlerHelper.copyStackWithSize(stack, toRemove);
		ItemStack remainder = remaining <= 0 ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(stack, remaining);
		slot.setNewStack(remainder);
		return removed;
	}

	@Override
	@NotNull
	public ItemStack removeItemNoUpdate(int index) {
		if (indexInvalid(index))
			return ItemStack.EMPTY;
		ItemStackHandlerSlot slot = getSlot(index);
		ItemStack stack = slot.getStack();
		slot.setNewStack(ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, @NotNull ItemStack stack) {
		if (indexInvalid(slot))
			return;
		setStackInSlot(slot, stack);
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
		if (indexInvalid(index))
			return false;
		return isItemValid(index, ItemVariant.of(stack));
	}

	@Override
	public int countItem(@NotNull Item item) {
		int total = 0;
		for (ItemStackHandlerSlot slot : getSlotsContaining(item)) {
			total += slot.getStack().getCount();
		}
		return total;
	}

	@Override
	public boolean hasAnyOf(Set<Item> set) {
		for (Item item : set) {
			if (!getSlotsContaining(item).isEmpty())
				return true;
		}
		return false;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < getSlotCount(); i++) {
			setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	public boolean indexInvalid(int slot) {
		return slot < 0 || slot >= getSlotCount();
	}
}
