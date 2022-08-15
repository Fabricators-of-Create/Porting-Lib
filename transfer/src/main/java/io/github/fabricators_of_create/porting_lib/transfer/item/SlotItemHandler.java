package io.github.fabricators_of_create.porting_lib.transfer.item;

import javax.annotation.Nonnull;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandler extends Slot {
	private static final Container emptyInventory = new SimpleContainer(0);
	private final ItemStackHandler itemHandler;
	private final int index;

	public SlotItemHandler(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.itemHandler = itemHandler;
		this.index = index;
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		if (stack.isEmpty())
			return false;
		return itemHandler.isItemValid(index, ItemVariant.of(stack));
	}

	@Override
	@Nonnull
	public ItemStack getItem() {
		return this.getItemHandler().getStackInSlot(index);
	}

	// Override if your IItemHandler does not implement IItemHandlerModifiable
	@Override
	public void set(@Nonnull ItemStack stack) {
		this.getItemHandler().setStackInSlot(index, stack);
		this.setChanged();
	}

	@Override
	public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {

	}

	@Override
	public int getMaxStackSize() {
		return this.itemHandler.getSlotLimit(this.index);
	}

	@Override
	public int getMaxStackSize(@Nonnull ItemStack stack) {
		return getItemHandler().getStackLimit(index, ItemVariant.of(stack));
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return !itemHandler.getStackInSlot(index).isEmpty();
	}

	@Override
	@Nonnull
	public ItemStack remove(int amount) {
		ItemStack held = itemHandler.getStackInSlot(index).copy();
		ItemStack removed = held.split(amount);
		itemHandler.setStackInSlot(index, held);
		return removed;
	}

	public ItemStackHandler getItemHandler() {
		return itemHandler;
	}
}
