package io.github.fabricators_of_create.porting_lib.transfer.item.wrapper;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface IInventoryStorage extends SlottedStackStorage {
	/**
	 * Retrieve an unmodifiable list of the wrappers for the slots in this inventory.
	 * Each wrapper corresponds to a single slot in the inventory.
	 */
	@Override
	@UnmodifiableView
	List<SingleSlotStorage<ItemVariant>> getSlots();

	@Override
	default int getSlotCount() {
		return getSlots().size();
	}

	@Override
	default SingleSlotStorage<ItemVariant> getSlot(int slot) {
		return getSlots().get(slot);
	}
}
