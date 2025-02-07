package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class SlotItemHandler extends Slot {
	private static final Inventory emptyInventory = new SimpleInventory(0);
	private final ItemStackHandler itemHandler;
	private final int index;

	public SlotItemHandler(ItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.itemHandler = itemHandler;
		this.id = index;
	}

	@Override
	public boolean canInsert(@NotNull ItemStack stack) {
		if (stack.isEmpty())
			return false;
		return itemHandler.isItemValid(id, ItemVariant.of(stack));
	}

	@Override
	@NotNull
	public ItemStack getStack() {
		return this.getItemHandler().getStackInSlot(id);
	}

	// Override if your IItemHandler does not implement IItemHandlerModifiable
	@Override
	public void setStack(@NotNull ItemStack stack) {
		this.getItemHandler().setStackInSlot(id, stack);
		this.markDirty();
	}

	@Override
	public void onQuickTransfer(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn) {

	}

	@Override
	public int getMaxItemCount() {
		return this.itemHandler.getSlotLimit(this.id);
	}

	@Override
	public int getMaxItemCount(@NotNull ItemStack stack) {
		return getItemHandler().getStackLimit(id, ItemVariant.of(stack));
	}

	@Override
	public boolean canTakeItems(PlayerEntity playerIn) {
		return !itemHandler.getStackInSlot(id).isEmpty();
	}

	@Override
	@NotNull
	public ItemStack takeStack(int amount) {
		ItemStack held = itemHandler.getStackInSlot(id).copy();
		ItemStack removed = held.split(amount);
		itemHandler.setStackInSlot(id, held);
		return removed;
	}

	public ItemStackHandler getItemHandler() {
		return itemHandler;
	}
}
