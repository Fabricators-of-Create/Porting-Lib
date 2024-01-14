package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Collections;
import java.util.List;

public class ItemStackHandlerContainerItemContext implements ContainerItemContext {
	protected final SingleSlotStorage<ItemVariant> wrapped;

	public ItemStackHandlerContainerItemContext(ItemStackHandler handler, int slot) {
		this.wrapped = new SingleSlotStorage<>() {
			@Override
			public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
				return handler.insert(resource, maxAmount, transaction);
			}

			@Override
			public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
				return handler.extract(resource, maxAmount, transaction);
			}

			@Override
			public boolean isResourceBlank() {
				return ItemVariant.of(handler.getStackInSlot(slot)).isBlank();
			}

			@Override
			public ItemVariant getResource() {
				return ItemVariant.of(handler.getStackInSlot(slot));
			}

			@Override
			public long getAmount() {
				return handler.getStackInSlot(slot).getCount();
			}

			@Override
			public long getCapacity() {
				return handler.getStackInSlot(slot).getMaxStackSize();
			}
		};
	}

	@Override
	public SingleSlotStorage<ItemVariant> getMainSlot() {
		return wrapped;
	}

	@Override
	public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
		return 0;
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
		return Collections.emptyList();
	}
}
