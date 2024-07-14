package io.github.fabricators_of_create.porting_lib.transfer.item;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;

import net.minecraft.core.HolderLookup;

import net.minecraft.nbt.Tag;

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
		this.lastStack = initial.copy();
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
		this.lastStack = PortingLib.DEBUG ? stack : stack.copy();
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
	public Tag save(HolderLookup.Provider provider, Tag tag) {
		return stack.save(provider, tag);
	}

	public void load(HolderLookup.Provider provider, CompoundTag tag) {
		ItemStack.parse(provider, tag).ifPresent(this::setStack);
		onStackChange();
		// intentionally do not notify handler, matches forge
	}
}
