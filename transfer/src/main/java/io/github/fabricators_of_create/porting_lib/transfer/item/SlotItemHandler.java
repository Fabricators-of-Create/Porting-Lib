package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandler extends Slot {
	private static final Container emptyInventory = new SimpleContainer(0);
	private final SlotExposedStorage itemHandler;
	private final int index;

	public SlotItemHandler(SlotExposedStorage itemHandler, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.itemHandler = itemHandler;
		this.index = index;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.isEmpty())
			return false;
		return itemHandler.isItemValid(index, ItemVariant.of(stack), stack.getCount());
	}

	@Override
	public ItemStack getItem() {
		return itemHandler.getStackInSlot(index);
	}

	// Override if your IItemHandler does not implement IItemHandlerModifiable
	@Override
	public void set(ItemStack stack) {
		itemHandler.setStackInSlot(index, stack);
		this.setChanged();
	}

	@Override
	public void initialize(ItemStack stack) {
		set(stack);
	}

	@Override
	public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
	}

	@Override
	public int getMaxStackSize() {
		return this.itemHandler.getSlotLimit(this.index);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return getItemHandler().getStackLimit(index, ItemVariant.of(stack), stack.getCount());
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return !itemHandler.getStackInSlot(index).isEmpty();
	}

	@Override
	public ItemStack remove(int amount) {
		ItemStack held = itemHandler.getStackInSlot(index).copy();
		ItemStack removed = held.split(amount);
		itemHandler.setStackInSlot(index, held);
		return removed;
	}

	public SlotExposedStorage getItemHandler() {
		return itemHandler;
	}
}
