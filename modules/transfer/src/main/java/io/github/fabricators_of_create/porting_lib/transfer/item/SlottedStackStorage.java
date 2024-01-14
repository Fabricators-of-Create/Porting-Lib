package io.github.fabricators_of_create.porting_lib.transfer.item;

import java.util.Iterator;

import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public interface SlottedStackStorage extends SlottedStorage<ItemVariant> {
	/**
	 * Get the ItemStack currently stored in the given slot.
	 * This stack should never be modified.
	 * Use {@link #setStackInSlot(int, ItemStack)} for modification.
	 */
	ItemStack getStackInSlot(int slot);

	/**
	 * Set the stack in the given slot. Once set, the provided stack should NOT be mutated, as it is not copied.
	 */
	void setStackInSlot(int slot, ItemStack stack);

	/**
	 * Determines the maximum amount of items the given slot can hold.
	 */
	int getSlotLimit(int slot);


	/**
	 * Use {@link SlottedStackStorage#isItemValid(int, ItemVariant, int)} as a replacement which takes in an extra count parameter
	 */
	@Deprecated(forRemoval = true)
	default boolean isItemValid(int slot, ItemVariant resource) {
		return isItemValid(slot, resource, 1);
	}

	/**
	 * Determines if the given ItemVariant can be stored in the given slot.
	 * @param slot the slot the resource is in
	 * @param resource the resource to check.
	 * @param count Note: the count parameter isn't always passed, in these cases count will always be 1
	 * @return
	 */
	default boolean isItemValid(int slot, ItemVariant resource, int count) {
		return true;
	}

	/**
	 * Insert into a specific slot only.
	 */
	default long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return getSlot(slot).insert(resource, maxAmount, transaction);
	}

	/**
	 * Extract from a specific slot only.
	 */
	default long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return getSlot(slot).extract(resource, maxAmount, transaction);
	}

	@Override
	default Iterator<StorageView<ItemVariant>> iterator() {
		//noinspection unchecked,rawtypes
		return (Iterator) getSlots().iterator();
	}
}
