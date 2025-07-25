package io.github.fabricators_of_create.porting_lib.transfer.item.wrapper;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class EmptySingleItemSlotStorage implements SingleSlotStorage<ItemVariant> {
	public static final EmptySingleItemSlotStorage INSTANCE = new EmptySingleItemSlotStorage();

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public boolean isResourceBlank() {
		return true;
	}

	@Override
	public ItemVariant getResource() {
		return ItemVariant.blank();
	}

	@Override
	public long getAmount() {
		return 0;
	}

	@Override
	public long getCapacity() {
		return 0;
	}
}
