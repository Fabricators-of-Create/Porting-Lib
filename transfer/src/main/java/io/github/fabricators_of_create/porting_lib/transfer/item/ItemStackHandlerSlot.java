package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemStackHandlerSlot extends SingleStackStorage {
	private final int index;
	private final ItemStackHandler handler;
	private ItemStack stack;
	private ItemStack lastStack; // last stack pre-transaction
	private ItemVariant variant;

	public ItemStackHandlerSlot(int index, ItemStackHandler handler, ItemStack initial) {
		this.index = index;
		this.handler = handler;
		this.lastStack = initial;
		this.setStack(initial);
		handler.initSlot(this);
	}

	@Override
	protected boolean canInsert(ItemVariant itemVariant) {
		return handler.isItemValid(index, itemVariant, 1);
	}

	@Override
	protected int getCapacity(ItemVariant itemVariant) {
		return handler.getStackLimit(index, itemVariant);
	}

	@Override
	protected ItemStack getStack() {
		return stack;
	}

	/**
	 * Should only be used in transactions.
	 */
	@Override
	protected void setStack(ItemStack stack) {
		this.stack = stack;
		this.variant = ItemVariant.of(stack);
	}

	public void setNewStack(ItemStack stack) {
		setStack(stack);
		onFinalCommit();
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
		onStackChange();
		notifyHandlerOfChange();
	}

	protected void onStackChange() {
		handler.onStackChange(this, lastStack, stack);
		this.lastStack = stack;
	}

	protected void notifyHandlerOfChange() {
		handler.onContentsChanged(index);
	}

	/**
	 * Save this slot to a new NBT tag.
	 * Note that "Slot" is a reserved key.
	 * @return null to skip saving this slot
	 */
	@Nullable
	public CompoundTag save() {
		return stack.isEmpty() ? null : stack.save(new CompoundTag());
	}

	public void load(CompoundTag tag) {
		setStack(ItemStack.of(tag));
		onStackChange();
		// intentionally do not notify handler, matches forge
	}
}
