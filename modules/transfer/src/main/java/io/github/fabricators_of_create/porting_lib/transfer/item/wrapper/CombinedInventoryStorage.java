package io.github.fabricators_of_create.porting_lib.transfer.item.wrapper;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CombinedInventoryStorage extends CombinedSlottedStorage<ItemVariant, SlottedStackStorage> implements SlottedStackStorage {
	protected final int[] baseIndex;
	protected final int slotCount;

	public CombinedInventoryStorage(SlottedStackStorage... storages) {
		super(List.of(storages));

		this.baseIndex = new int[storages.length];
		int index = 0;

		for (int i = 0; i < storages.length; i++) {
			index += storages[i].getSlotCount();
			baseIndex[i] = index;
		}

		this.slotCount = index;
	}

	protected int getIndexForSlot(int slot) {
		if (slot < 0)
			return -1;

		for (int i = 0; i < baseIndex.length; i++) {
			if (slot - baseIndex[i] < 0)
				return i;
		}

		return -1;
	}

	protected SlottedStackStorage getHandlerFromIndex(int index) {
		if (index < 0 || index >= this.parts.size()) {
			return EmptyItemHandler.INSTANCE;
		}

		return this.parts.get(index);
	}

	protected int getSlotFromIndex(int slot, int index) {
		if (index <= 0 || index >= baseIndex.length) {
			return slot;
		}

		return slot - baseIndex[index - 1];
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		int index = getIndexForSlot(slot);
		SlottedStackStorage handler = getHandlerFromIndex(index);
		slot = getSlotFromIndex(slot, index);
		return handler.getStackInSlot(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		int index = getIndexForSlot(slot);
		SlottedStackStorage handler = getHandlerFromIndex(index);
		slot = getSlotFromIndex(slot, index);
		handler.setStackInSlot(slot, stack);
	}

	@Override
	public int getSlotLimit(int slot) {
		int index = getIndexForSlot(slot);
		SlottedStackStorage handler = getHandlerFromIndex(index);
		int localSlot = getSlotFromIndex(slot, index);
		return handler.getSlotLimit(localSlot);
	}
}
