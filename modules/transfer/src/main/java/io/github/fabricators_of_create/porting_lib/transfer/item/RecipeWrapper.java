package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

/**
 * Wraps an ItemStackHandler in a Container for use in recipes and crafting.
 * @deprecated use of this class is discouraged, ItemStackHandlerContainer should fit all use cases.
 */
@Deprecated
public class RecipeWrapper extends ItemStackHandler implements Container {
	protected final ItemStackHandler handler;

	public RecipeWrapper(ItemStackHandler handler) {
		super(0);
		this.handler = handler;
	}

	@Override
	public int getContainerSize() {
		return handler.getSlotCount();
	}

	@Override
	public boolean isEmpty() {
		return handler.empty();
	}

	@Override
	@NotNull
	public ItemStack getItem(int index) {
		return handler.getStackInSlot(index);
	}

	@Override
	@NotNull
	public ItemStack removeItem(int index, int count) {
		if (index >= 0 && index < handler.getSlotCount()) {
			ItemStack current = handler.getStackInSlot(index);
			if (current.isEmpty())
				return ItemStack.EMPTY;
			current = current.copy();
			ItemStack extracted = current.split(count);
			handler.setStackInSlot(index, current);
			return extracted;
		}
		return ItemStack.EMPTY;
	}

	@Override
	@NotNull
	public ItemStack removeItemNoUpdate(int index) {
		return removeItem(index, Integer.MAX_VALUE);
	}

	@Override
	public void setItem(int index, @NotNull ItemStack stack) {
		handler.setStackInSlot(index, stack);
	}

	@Override
	public void clearContent() {
		handler.setSize(handler.getSlotCount());
	}

	@Override
	public int getMaxStackSize() { return 0; }
	@Override
	public void setChanged() {}
	@Override
	public boolean stillValid(@NotNull Player player) { return false; }

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return handler.insert(resource, maxAmount, transaction);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return handler.extract(resource, maxAmount, transaction);
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return handler.iterator();
	}

	@Override
	public Iterable<StorageView<ItemVariant>> nonEmptyViews() {
		return handler.nonEmptyViews();
	}

	@Override
	public Iterator<StorageView<ItemVariant>> nonEmptyIterator() {
		return handler.nonEmptyIterator();
	}

	@Override
	public int getSlotCount() {
		return handler.getSlotCount();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return handler.getStackInSlot(slot);
	}

	@Override
	public ItemVariant getVariantInSlot(int slot) {
		return handler.getVariantInSlot(slot);
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
	public boolean isItemValid(int slot, ItemVariant resource, int count) {
		return handler.isItemValid(slot, resource, count);
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
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		return handler.serializeNBT(provider);
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
		handler.deserializeNBT(provider, nbt);
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
	public long getVersion() {
		return handler.getVersion();
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
	public void onContentsChanged(int slot) {
		handler.onContentsChanged(slot);
	}

	@Override
	public ItemStackHandlerSlot getSlot(int slot) {
		return handler.getSlot(slot);
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		return handler.getSlots();
	}

	@Override
	void onStackChange(ItemStackHandlerSlot slot, ItemStack oldStack, ItemStack newStack) {
		handler.onStackChange(slot, oldStack, newStack);
	}

	@Override
	void initSlot(ItemStackHandlerSlot slot) {
		handler.initSlot(slot);
	}
}
