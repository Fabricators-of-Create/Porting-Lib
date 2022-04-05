package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext.Result;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ItemStackHandlerSlotView extends SnapshotParticipant<ItemStack> implements StorageView<ItemVariant> {
	protected ItemStackHandler handler;
	protected int index;
	protected ItemStack stack;
	protected ItemVariant variant;

	public ItemStackHandlerSlotView(ItemStackHandler handler, int index) {
		this.handler = handler;
		this.index = index;
		this.stack = handler.stacks[index];
		this.variant = ItemVariant.of(stack);
	}

	private void setStack(ItemStack stack, @Nullable TransactionContext ctx) {
		handler.contentsChangedInternal(index, stack, ctx);
		this.stack = stack;
		this.variant = ItemVariant.of(stack);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		int extracted = 0;
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
		return stack.isEmpty();
	}

	@Override
	public ItemVariant getResource() {
		return variant;
	}

	@Override
	public long getAmount() {
		return stack.getCount();
	}

	@Override
	public long getCapacity() {
		return stack.getMaxStackSize();
	}

	@Override
	protected ItemStack createSnapshot() {
		return stack.copy();
	}

	@Override
	protected void readSnapshot(ItemStack snapshot) {
		setStack(snapshot, null);
	}

	@Override
	public String toString() {
		return "ItemStackHandlerSlotView{" +
				"index=" + index +
				", stack=" + stack +
				'}';
	}

	@Override
	protected void onFinalCommit() {
		handler.onFinalCommit();
	}
}
