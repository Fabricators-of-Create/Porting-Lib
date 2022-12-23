package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public interface SlotExposedStorage extends Storage<ItemVariant> {
	ItemStack getStackInSlot(int slot);
	int getSlots();
	int getSlotLimit(int slot);
	int getStackLimit(int slot, ItemVariant resource, long amount);
	default void setStackInSlot(int slot, @NotNull ItemStack stack) {

	}
	boolean isItemValid(int slot, ItemVariant resource, long amount);
}
