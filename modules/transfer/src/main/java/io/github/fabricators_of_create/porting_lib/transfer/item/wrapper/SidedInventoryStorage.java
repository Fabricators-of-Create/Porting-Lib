package io.github.fabricators_of_create.porting_lib.transfer.item.wrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;

/**
 * Sidedness-aware wrapper around a {@link InventoryStorage} for sided inventories.
 */
class SidedInventoryStorage extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements IInventoryStorage {
	private final InventoryStorage backingStorage;

	SidedInventoryStorage(InventoryStorage storage, Direction direction) {
		super(Collections.unmodifiableList(createWrapperList(storage, direction)));
		this.backingStorage = storage;
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		return parts;
	}

	private static List<SingleSlotStorage<ItemVariant>> createWrapperList(InventoryStorage storage, Direction direction) {
		WorldlyContainer inventory = (WorldlyContainer) storage.inventory;
		int[] availableSlots = inventory.getSlotsForFace(direction);
		SidedInventorySlotWrapper[] slots = new SidedInventorySlotWrapper[availableSlots.length];

		for (int i = 0; i < availableSlots.length; ++i) {
			slots[i] = new SidedInventorySlotWrapper(storage.backingList.get(availableSlots[i]), inventory, direction);
		}

		return Arrays.asList(slots);
	}

	@Override
	public String toString() {
		// These two are the same from the user's perspective.
		return backingStorage.toString();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return backingStorage.getStackInSlot(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		backingStorage.setStackInSlot(slot, stack);
	}

	@Override
	public int getSlotLimit(int slot) {
		return backingStorage.getSlotLimit(slot);
	}
}
