package io.github.fabricators_of_create.porting_lib.transfer.item;

import org.jetbrains.annotations.NotNull;

import io.github.fabricators_of_create.porting_lib.PortingConstants;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public interface SlotExposedStorage extends Storage<ItemVariant> {
	ItemStack getStackInSlot(int slot);
	int getSlots();
	int getSlotLimit(int slot);
	default void setStackInSlot(int slot, @NotNull ItemStack stack) {

	}
	boolean isItemValid(int slot, ItemVariant resource, long amount);

	default long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		PortingConstants.LOGGER.warn("Tried to insert to a slotted storage without a implementation! Implementations will be forced to implement this in 1.19.3");
		if (isItemValid(slot, resource, maxAmount))
			return insert(resource, maxAmount, transaction);
		return 0;
	}

	default long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		PortingConstants.LOGGER.warn("Tried to extract from a slotted storage without a implementation! Implementations will be forced to implement this in 1.19.3");
		return extract(resource, maxAmount, transaction);
	}
}
