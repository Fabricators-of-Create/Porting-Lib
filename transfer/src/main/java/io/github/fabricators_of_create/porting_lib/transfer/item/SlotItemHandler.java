package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandler extends Slot {
	private static final Container emptyInventory = new SimpleContainer(0);
	private final SlottedStackStorage storage;
	private final int index;

	public SlotItemHandler(SlottedStackStorage storage, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.storage = storage;
		this.index = index;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.isEmpty())
			return false;
		return storage.isItemValid(index, ItemVariant.of(stack));
	}

	@Override
	@NotNull
	public ItemStack getItem() {
		return storage.getStackInSlot(index);
	}

	// Override if your IItemHandler does not implement IItemHandlerModifiable
	@Override
	public void set(@NotNull ItemStack stack) {
		storage.setStackInSlot(index, stack);
		this.setChanged();
	}

	@Override
	public void onQuickCraft(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn) {
	}

	@Override
	public int getMaxStackSize() {
		return this.storage.getSlotLimit(this.index);
	}

	@Override
	public int getMaxStackSize(@NotNull ItemStack stack) {
		return getItemHandler().getSlotLimit(index);
	}

	@Override
	public boolean mayPickup(@NotNull Player playerIn) {
		return !storage.getStackInSlot(index).isEmpty();
	}

	@Override
	@NotNull
	public ItemStack remove(int amount) {
		ItemStack held = storage.getStackInSlot(index).copy();
		ItemStack removed = held.split(amount);
		storage.setStackInSlot(index, held);
		return removed;
	}

	public SlottedStackStorage getItemHandler() {
		return storage;
	}
}
