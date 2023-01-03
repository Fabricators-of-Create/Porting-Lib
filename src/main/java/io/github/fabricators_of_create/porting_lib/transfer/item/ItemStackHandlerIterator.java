package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Iterator;

public class ItemStackHandlerIterator implements Iterator<StorageView<ItemVariant>> {
	protected boolean open = true;
	protected int index = 0;
	protected ItemStackHandler handler;

	public ItemStackHandlerIterator(ItemStackHandler handler, TransactionContext t) {
		this.handler = handler;
		t.addCloseCallback((transaction, result) -> open = false);
	}

	@Override
	public boolean hasNext() {
		return open && index < handler.getSlots();
	}

	@Override
	public StorageView<ItemVariant> next() {
		index++;
		return new ItemStackHandlerSlotView(handler, index - 1);
	}
}
