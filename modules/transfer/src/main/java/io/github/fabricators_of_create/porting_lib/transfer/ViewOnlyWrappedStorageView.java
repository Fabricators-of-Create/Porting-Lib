package io.github.fabricators_of_create.porting_lib.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public record ViewOnlyWrappedStorageView<T>(StorageView<T> wrapped) implements StorageView<T> {

	@Override
	public long extract(T resource, long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public boolean isResourceBlank() {
		return wrapped.isResourceBlank();
	}

	@Override
	public T getResource() {
		return wrapped.getResource();
	}

	@Override
	public long getAmount() {
		return wrapped.getAmount();
	}

	@Override
	public long getCapacity() {
		return wrapped.getCapacity();
	}
}
