package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ItemStackHandlerSlotView extends SnapshotParticipant<ItemStack> implements StorageView<ItemVariant> {
	protected ItemStackHandler handler;
	protected int index;

	public ItemStackHandlerSlotView(ItemStackHandler handler, int index) {
		this.handler = handler;
		this.index = index;
	}

	private void setStack(ItemStack stack, @Nullable TransactionContext ctx) {
		handler.contentsChangedInternal(index, stack, ctx);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		int extracted = 0;
		ItemStack stack = getStack();
		if (resource.matches(stack)) {
			extracted = (int) Math.min(stack.getCount(), maxAmount);
			if (extracted > 0) {
				updateSnapshots(transaction);
				int remaining = stack.getCount() - extracted;
				if (remaining <= 0) {
					setStack(ItemStack.EMPTY, transaction);
				} else {
					ItemStack newStack = stack.copy();
					newStack.setCount(remaining);
					setStack(newStack, transaction);
				}
			}
		}
		return extracted;
	}

	@Override
	public boolean isResourceBlank() {
		return getResource().isBlank();
	}

	@Override
	public ItemVariant getResource() {
		return handler.getVariantInSlot(index);
	}

	@Override
	public long getAmount() {
		return getStack().getCount();
	}

	@Override
	public long getCapacity() {
		return handler.getSlotLimit(index);
	}

	@Override
	protected ItemStack createSnapshot() {
		return getStack().copy();
	}

	@Override
	protected void readSnapshot(ItemStack snapshot) {
		setStack(snapshot, null);
	}

	@Override
	public String toString() {
		return "ItemStackHandlerSlotView{" + index + '}';
	}

	@Override
	protected void onFinalCommit() {
		handler.onFinalCommit();
	}

	private ItemStack getStack() {
		return handler.getStackInSlot(index);
	}
}
