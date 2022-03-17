package io.github.fabricators_of_create.porting_lib.transfer.item;

import io.github.fabricators_of_create.porting_lib.util.ItemStackUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Iterator;

public class ItemStackHandler extends SnapshotParticipant<ItemStack[]> implements Storage<ItemVariant> {
	public ItemStack[] stacks;

	public ItemStackHandler() {
		this(1);
	}

	public ItemStackHandler(int stacks) {
		this.stacks = new ItemStack[stacks];
		Arrays.fill(this.stacks, ItemStack.EMPTY);
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long inserted = 0;
		updateSnapshots(transaction);
		for (int i = 0; i < stacks.length; i++) {
			ItemStack held = stacks[i];
			if (held.isEmpty()) { // just throw in a full stack
				int toFill = (int) Math.min(resource.getItem().getMaxStackSize(), maxAmount);
				maxAmount -= toFill;
				inserted += toFill;
				ItemStack stack = resource.toStack(toFill);
				stacks[i] = stack;
			} else if (ItemStackUtil.canItemStacksStack(held, resource.toStack())) { // already filled, but can stack
				int max = held.getMaxStackSize(); // total possible
				int canInsert = max - held.getCount(); // room available
				int actuallyInsert = Math.min(canInsert, (int) maxAmount);
				maxAmount -= actuallyInsert;
				inserted += actuallyInsert;
				ItemStack newStack = resource.toStack(actuallyInsert);
				stacks[i] = newStack;
			}
			if (maxAmount == 0)
				break;
		}
		return inserted;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long extracted = 0;
		updateSnapshots(transaction);
		for (int i = 0; i < stacks.length; i++) {
			ItemStack stack = stacks[i];
			if (resource.matches(stack)) {
				// find how much to remove
				int stored = stack.getCount();
				int toRemove = (int) Math.min(stored, maxAmount);
				maxAmount -= toRemove;
				extracted += toRemove;
				// remove from storage
				stack.setCount(stack.getCount() - toRemove);
				if (stack.isEmpty()) // set to empty for a clean list
					stacks[i] = ItemStack.EMPTY;
				if (maxAmount == 0) // nothing left to extract - exit
					break;
			}
		}
		return extracted;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
		return new ItemStackHandlerIterator(this, transaction);
	}

	@Override
	protected ItemStack[] createSnapshot() {
		ItemStack[] array = new ItemStack[stacks.length];
		System.arraycopy(stacks, 0, array, 0, stacks.length);
		return array;
	}

	@Override
	protected void readSnapshot(ItemStack[] snapshot) {
		this.stacks = snapshot;
	}

	@Override
	public String toString() {
		return "ItemStackHandler{" +
				"stacks=" + Arrays.toString(stacks) +
				'}';
	}
}
