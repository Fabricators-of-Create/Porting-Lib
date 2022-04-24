package io.github.fabricators_of_create.porting_lib.transfer.item;

import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionSuccessCallback;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler.SnapshotData;
import io.github.fabricators_of_create.porting_lib.util.ItemStackUtil;
import io.github.fabricators_of_create.porting_lib.util.NBTSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.Iterator;

public class ItemStackHandler extends SnapshotParticipant<SnapshotData> implements Storage<ItemVariant>, NBTSerializable {
	public ItemStack[] stacks;
	public boolean client = false;

	public ItemStackHandler() {
		this(1);
	}

	public ItemStackHandler(int stacks) {
		this.stacks = new ItemStack[stacks];
		Arrays.fill(this.stacks, ItemStack.EMPTY);
	}

	public ItemStackHandler(ItemStack[] stacks) {
		this.stacks = stacks;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long inserted = 0;
		updateSnapshots(transaction);
		for (int i = 0; i < stacks.length; i++) {
			if (isItemValid(i, resource)) {
				ItemStack held = stacks[i];
				if (held.isEmpty()) { // just throw in a full stack
					int toFill = (int) Math.min(getStackLimit(i, resource), maxAmount);
					maxAmount -= toFill;
					inserted += toFill;
					ItemStack stack = resource.toStack(toFill);
					contentsChangedInternal(i, stack, transaction);
				} else if (ItemStackUtil.canItemStacksStack(held, resource.toStack())) { // already filled, but can stack
					int max = getStackLimit(i, resource); // total possible
					int canInsert = max - held.getCount(); // room available
					int actuallyInsert = Math.min(canInsert, (int) maxAmount);
					if (actuallyInsert > 0) {
						maxAmount -= actuallyInsert;
						inserted += actuallyInsert;
						held = held.copy();
						held.grow(actuallyInsert);
						contentsChangedInternal(i, held, transaction);
					}
				}
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
		TransactionSuccessCallback callback = new TransactionSuccessCallback(transaction);
		for (int i = 0; i < stacks.length; i++) {
			ItemStack stack = stacks[i];
			if (resource.matches(stack)) {
				// find how much to remove
				int stored = stack.getCount();
				int toRemove = (int) Math.min(stored, maxAmount);
				maxAmount -= toRemove;
				extracted += toRemove;
				// remove from storage
				stack = stack.copy();
				stack.setCount(stack.getCount() - toRemove);
				int finalI = i;
				callback.addCallback(() -> onContentsChanged(finalI));
				if (stack.isEmpty()) // set to empty for a clean list
					stack = ItemStack.EMPTY;
				stacks[i] = stack;
				if (maxAmount == 0) // nothing left to extract - exit
					break;
			}
		}
		return extracted;
	}

	protected void contentsChangedInternal(int slot, ItemStack newStack, @Nullable TransactionContext ctx) {
		stacks[slot] = newStack;
		if (ctx != null) TransactionCallback.onSuccess(ctx, () -> onContentsChanged(slot));
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
		return new ItemStackHandlerIterator(this, transaction);
	}

	@Override
	protected SnapshotData createSnapshot() {
		ItemStack[] array = new ItemStack[stacks.length];
		System.arraycopy(stacks, 0, array, 0, stacks.length);
		return new SnapshotData(array);
	}

	@Override
	protected void readSnapshot(SnapshotData snapshot) {
		this.stacks = snapshot.stacks;
	}

	@Override
	public String toString() {
		return  getClass().getSimpleName() + '{' + "stacks=" + Arrays.toString(stacks) + '}';
	}

	public int getSlots() {
		return stacks.length;
	}

	public void setStackInSlot(int slot, ItemStack stack) {
		stacks[slot] = stack;
		onContentsChanged(slot);
	}

	// do not modify this stack!
	public ItemStack getStackInSlot(int slot) {
		return stacks[slot];
	}

	public int getSlotLimit(int slot) {
		return getStackInSlot(slot).getMaxStackSize();
	}

	protected int getStackLimit(int slot, ItemVariant resource) {
		return Math.min(getSlotLimit(slot), resource.getItem().getMaxStackSize());
	}

	public boolean isItemValid(int slot, ItemVariant resource) {
		return true;
	}

	protected void onLoad() {
	}

	protected void onContentsChanged(int slot) {
	}

	public void setSize(int size) {
		this.stacks = new ItemStack[size];
		Arrays.fill(this.stacks, ItemStack.EMPTY);
	}

	@Override
	public CompoundTag serializeNBT() {
		ListTag nbtTagList = new ListTag();
		for (int i = 0; i < stacks.length; i++) {
			ItemStack stack = stacks[i];
			if (!stack.isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", i);
				stack.save(itemTag);
				nbtTagList.add(itemTag);
			}
		}
		CompoundTag nbt = new CompoundTag();
		nbt.put("Items", nbtTagList);
		nbt.putInt("Size", stacks.length);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.length);
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < stacks.length) {
				stacks[slot] = ItemStack.of(itemTags);
			}
		}
		onLoad();
	}

	public static class SnapshotData {
		public final ItemStack[] stacks;

		public SnapshotData(ItemStack[] stacks) {
			this.stacks = stacks;
		}
	}

	@Override
	protected void onFinalCommit() {
		super.onFinalCommit();
	}
}
