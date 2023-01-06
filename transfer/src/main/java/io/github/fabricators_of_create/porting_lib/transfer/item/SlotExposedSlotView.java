package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class SlotExposedSlotView implements StorageView<ItemVariant> {
	protected SlotExposedStorage handler;
	protected int index;

	public SlotExposedSlotView(SlotExposedStorage handler, int index) {
		this.handler = handler;
		this.index = index;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return handler.extractSlot(index, resource, maxAmount, transaction);
	}

	@Override
	public boolean isResourceBlank() {
		return getResource().isBlank();
	}

	@Override
	public ItemVariant getResource() {
		return ItemVariant.of(handler.getStackInSlot(index));
	}

	@Override
	public long getAmount() {
		return handler.getStackInSlot(index).getCount();
	}

	@Override
	public long getCapacity() {
		return handler.getStackInSlot(index).getMaxStackSize();
	}

	@Override
	public String toString() {
		return "ItemStackHandlerSlotView{" +
				"index=" + index +
				", stack=" + handler.getStackInSlot(index) +
				'}';
	}
}
