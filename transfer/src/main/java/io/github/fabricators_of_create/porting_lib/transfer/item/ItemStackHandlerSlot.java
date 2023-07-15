package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;

public class ItemStackHandlerSlot extends SingleStackStorage {
	private final int index;
	private final ItemStackHandler handler;
	private ItemStack stack = ItemStack.EMPTY;
	private ItemVariant variant = ItemVariant.blank();

	public ItemStackHandlerSlot(int index, ItemStackHandler handler, ItemStack initial) {
		this.index = index;
		this.handler = handler;
		this.setStack(initial);
	}

	@Override
	public boolean canInsert(ItemVariant itemVariant) {
		return handler.isItemValid(index, itemVariant);
	}

	@Override
	public int getCapacity(ItemVariant itemVariant) {
		return handler.getStackLimit(index, itemVariant);
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}

	@Override
	public void setStack(ItemStack stack) {
		handler.onStackChange(this, this.stack, stack);
		this.stack = stack;
		this.variant = ItemVariant.of(stack);
	}

	@Override
	public ItemVariant getResource() {
		return variant;
	}

	public int getIndex() {
		return index;
	}

	@Override
	protected void onFinalCommit() {
		handler.onContentsChanged(index);
	}
}
