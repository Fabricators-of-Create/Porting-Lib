package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext.Result;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Wraps an ItemStackHandler in a Container for use in recipes and crafting.
 */
public class RecipeWrapper extends ItemStackHandler implements Container {
	protected final ItemStackHandler handler;

	public RecipeWrapper(ItemStackHandler handler) {
		super(0);
		this.handler = handler;
	}

	@Override
	public int getContainerSize() {
		return handler.stacks.length;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : handler.stacks) {
			if (!stack.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int index) {
		return handler.getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack[] stacks = handler.stacks;
		if (index >= 0 && index < stacks.length) {
			ItemStack current = stacks[index];
			stacks[index] = ItemStack.EMPTY;
			return current.split(count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return removeItem(index, Integer.MAX_VALUE);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		ItemStack[] stacks = handler.stacks;
		if (index >= 0 && index < stacks.length) {
			stacks[index] = stack;
		}
	}

	@Override
	public void clearContent() {
		Arrays.fill(handler.stacks, ItemStack.EMPTY);
	}

	@Override
	public int getMaxStackSize() { return 0; }
	@Override
	public void setChanged() {}
	@Override
	public boolean stillValid(Player player) { return false; }
	@Override
	public void startOpen(Player player) {}
	@Override
	public void stopOpen(Player player) {}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return handler.insert(resource, maxAmount, transaction);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return handler.extract(resource, maxAmount, transaction);
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
		return handler.iterator(transaction);
	}

	@Override
	protected SnapshotData createSnapshot() {
		return handler.createSnapshot();
	}

	@Override
	protected void readSnapshot(SnapshotData snapshot) {
		handler.readSnapshot(snapshot);
	}

	@Override
	public int getSlots() {
		return handler.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return handler.getStackInSlot(slot);
	}

	@Override
	public int getSlotLimit(int slot) {
		return handler.getSlotLimit(slot);
	}

	@Override
	protected int getStackLimit(int slot, ItemVariant resource) {
		return handler.getStackLimit(slot, resource);
	}

	@Override
	public boolean isItemValid(int slot, ItemVariant resource) {
		return handler.isItemValid(slot, resource);
	}

	@Override
	protected void onLoad() {
		handler.onLoad();
	}

	@Override
	public void setSize(int size) {
		handler.setSize(size);
	}

	@Override
	public CompoundTag serializeNBT() {
		return handler.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		handler.deserializeNBT(nbt);
	}

	@Override
	public long simulateInsert(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		return handler.simulateInsert(resource, maxAmount, transaction);
	}

	@Override
	public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		return handler.simulateExtract(resource, maxAmount, transaction);
	}

	@Override
	public boolean supportsExtraction() {
		return handler.supportsExtraction();
	}

	@Override
	public boolean supportsInsertion() {
		return handler.supportsInsertion();
	}

	@Override
	public Iterable<StorageView<ItemVariant>> iterable(TransactionContext transaction) {
		return handler.iterable(transaction);
	}

	@Override
	public long getVersion() {
		return handler.getVersion();
	}

	@Override
	public @Nullable StorageView<ItemVariant> exactView(TransactionContext transaction, ItemVariant resource) {
		return handler.exactView(transaction, resource);
	}

	@Override
	public void afterOuterClose(Result result) {
		handler.afterOuterClose(result);
	}

	@Override
	public void onClose(TransactionContext transaction, Result result) {
		handler.onClose(transaction, result);
	}

	@Override
	public void updateSnapshots(TransactionContext transaction) {
		handler.updateSnapshots(transaction);
	}

	@Override
	public String toString() {
		return "RecipeWrapper{" + handler + "}";
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		handler.setStackInSlot(slot, stack);
	}

	@Override
	public void contentsChangedInternal(int slot, ItemStack newStack, TransactionContext ctx) {
		handler.contentsChangedInternal(slot, newStack, ctx);
	}

	@Override
	public void onContentsChanged(int slot) {
		handler.onContentsChanged(slot);
	}

	@Override
	protected void onFinalCommit() {
		handler.onFinalCommit();
	}
}
