package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

import java.util.Iterator;

public class ItemStackHandlerIterator implements Iterator<StorageView<ItemVariant>> {
	protected int index = 0;
	protected ItemStackHandler handler;

	public ItemStackHandlerIterator(ItemStackHandler handler) {
		this.handler = handler;
	}

	@Override
	public boolean hasNext() {
		return index < handler.stacks.length;
	}

	@Override
	public StorageView<ItemVariant> next() {
		index++;
		return new ItemStackHandlerSlotView(handler, index - 1);
	}
}
