package io.github.fabricators_of_create.porting_lib.transfer.item;

import org.jetbrains.annotations.NotNull;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandler extends Slot {
	private static final Container emptyInventory = new SimpleContainer(0);
	private final SlottedStorage<ItemVariant> storage;
	private final int index;

	public SlotItemHandler(SlottedStackStorage storage, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.storage = storage;
		this.index = index;
	}

	public SlotItemHandler(SlottedStorage<ItemVariant> storage, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.storage = storage;
		this.index = index;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.isEmpty())
			return false;
		if (storage instanceof SlottedStackStorage slottedStorage)
			return slottedStorage.isItemValid(index, ItemVariant.of(stack), stack.getCount());
		return true;
	}

	@Override
	@NotNull
	public ItemStack getItem() {
		if (storage instanceof SlottedStackStorage slottedStorage)
			return slottedStorage.getStackInSlot(index);
		var slot = storage.getSlot(index);
		return slot.getResource().toStack((int) slot.getAmount());
	}

	// Override if your IItemHandler does not implement IItemHandlerModifiable
	@Override
	public void set(@NotNull ItemStack stack) {
		if (storage instanceof SlottedStackStorage slottedStorage)
			slottedStorage.setStackInSlot(index, stack);
		else {
			var slot = storage.getSlot(index);
			if (!slot.isResourceBlank()) {
				try (Transaction t = TransferUtil.getTransaction()) {
					slot.extract(slot.getResource(), slot.getAmount(), t);
					t.commit();
				}
			}
			var variant = ItemVariant.of(stack);
			if (!variant.isBlank()) {
				try (Transaction t = TransferUtil.getTransaction()) {
					slot.insert(variant, stack.getCount(), t);
					t.commit();
				}
			}
		}
		this.setChanged();
	}

	@Override
	public void onQuickCraft(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn) {
	}

	@Override
	public int getMaxStackSize() {
		if (storage instanceof SlottedStackStorage slottedStorage)
			return slottedStorage.getSlotLimit(this.index);
		return (int) storage.getSlot(index).getCapacity();
	}

	@Override
	public int getMaxStackSize(@NotNull ItemStack stack) {
		if (storage instanceof SlottedStackStorage slottedStorage)
			return slottedStorage.getSlotLimit(index);
		return (int) storage.getSlot(index).getCapacity();
	}

	@Override
	public boolean mayPickup(@NotNull Player playerIn) {
		if (storage instanceof SlottedStackStorage slottedStorage)
			return !slottedStorage.getStackInSlot(index).isEmpty();
		return !storage.getSlot(index).isResourceBlank();
	}

	@Override
	@NotNull
	public ItemStack remove(int amount) {
		if (storage instanceof SlottedStackStorage slottedStorage) {
			ItemStack held = slottedStorage.getStackInSlot(index).copy();
			ItemStack removed = held.split(amount);
			slottedStorage.setStackInSlot(index, held);
			return removed;
		}
		try (Transaction t = TransferUtil.getTransaction()) {
			var slot = storage.getSlot(index);
			ItemStack lastResource = slot.getResource().toStack();
			long extraced = slot.extract(slot.getResource(), amount, t);
			t.commit();
			return lastResource.copyWithCount((int) extraced);
		}
	}

	public SlottedStorage<ItemVariant> getItemHandler() {
		return storage;
	}
}
