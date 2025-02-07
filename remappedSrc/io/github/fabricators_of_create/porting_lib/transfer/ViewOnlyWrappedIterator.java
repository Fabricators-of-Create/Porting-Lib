package io.github.fabricators_of_create.porting_lib.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

import java.util.Iterator;

public class ViewOnlyWrappedIterator<T> implements Iterator<StorageView<T>> {
	protected final Iterator<StorageView<T>> base;

	public ViewOnlyWrappedIterator(Iterator<StorageView<T>> base) {
		this.base = base;
	}

	@Override
	public boolean hasNext() {
		return base.hasNext();
	}

	@Override
	public StorageView<T> next() {
		StorageView<T> baseNext = base.next();
		return new ViewOnlyWrappedStorageView<>(baseNext);
	}
}
