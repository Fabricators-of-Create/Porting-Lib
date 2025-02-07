package io.github.fabricators_of_create.porting_lib.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class WrappedStorage<T> implements Storage<T> {
	protected Storage<T> wrapped;

	public WrappedStorage(Storage<T> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public boolean supportsInsertion() {
		return wrapped.supportsExtraction();
	}

	@Override
	public long insert(T resource, long maxAmount, TransactionContext transaction) {
		return wrapped.insert(resource, maxAmount, transaction);
	}

	@Override
	public long simulateInsert(T resource, long maxAmount, @Nullable TransactionContext transaction) {
		return wrapped.simulateInsert(resource, maxAmount, transaction);
	}

	@Override
	public boolean supportsExtraction() {
		return wrapped.supportsExtraction();
	}

	@Override
	public long extract(T resource, long maxAmount, TransactionContext transaction) {
		return wrapped.extract(resource, maxAmount, transaction);
	}

	@Override
	public long simulateExtract(T resource, long maxAmount, @Nullable TransactionContext transaction) {
		return wrapped.simulateExtract(resource, maxAmount, transaction);
	}

	@Override
	public Iterator<? extends StorageView<T>> iterator(TransactionContext transaction) {
		return wrapped.iterator(transaction);
	}

	@Override
	public Iterable<? extends StorageView<T>> iterable(TransactionContext transaction) {
		return wrapped.iterable(transaction);
	}

	@Override
	public @Nullable StorageView<T> exactView(TransactionContext transaction, T resource) {
		return wrapped.exactView(transaction, resource);
	}

	@Override
	public long getVersion() {
		return wrapped.getVersion();
	}
}
