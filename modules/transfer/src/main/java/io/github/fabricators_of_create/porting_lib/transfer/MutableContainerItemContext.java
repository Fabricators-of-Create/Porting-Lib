package io.github.fabricators_of_create.porting_lib.transfer;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public class MutableContainerItemContext implements ContainerItemContext {
	private final Slot slot;

	public MutableContainerItemContext(ItemStack initial) {
		this.slot = new Slot(initial);
	}

	@Override
	public SingleSlotStorage<ItemVariant> getMainSlot() {
		return this.slot;
	}

	@Override
	public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
		return 0;
	}

	@Override
	@UnmodifiableView
	public List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
		return List.of();
	}

	private static class Slot extends SingleItemStorage {
		public Slot(ItemStack initial) {
			this.variant = ItemVariant.of(initial);
			this.amount = initial.getCount();
		}

		@Override
		protected long getCapacity(ItemVariant variant) {
			return variant.getItem().getDefaultMaxStackSize();
		}
	}
}
