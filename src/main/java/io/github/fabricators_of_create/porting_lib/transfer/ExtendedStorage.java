package io.github.fabricators_of_create.porting_lib.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Iterator;

/**
 * An extension of {@link Storage} providing extra functionality that implementations may control.
 */
public interface ExtendedStorage<T> extends Storage<T> {
	ResourceAmount<T> extractAny(long maxAmount, TransactionContext transaction);

	/**
	 * @return an iterator of only StorageViews that are not empty.
	 */
	Iterator<? extends StorageView<T>> nonEmptyViews();

	@SuppressWarnings("unchecked, rawtypes")
	default Iterable<? extends StorageView<T>> nonEmptyIterable() {
		return () -> (Iterator) nonEmptyViews();
	}
}
