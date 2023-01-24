package io.github.fabricators_of_create.porting_lib.transfer.item;

import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler.SnapshotData;
import io.github.fabricators_of_create.porting_lib.util.INBTSerializable;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ItemStackHandler extends SnapshotParticipant<SnapshotData> implements Storage<ItemVariant>, INBTSerializable<CompoundTag> {
	private static final ItemVariant blank = ItemVariant.blank();

	/**
	 * Do not directly access this array. It must be kept in sync with the others. Restricting access may break existing mods.
	 */
	@Deprecated
	public ItemStack[] stacks;
	protected ItemVariant[] variants;
	protected Map<Item, IntSortedSet> lookup;

	public ItemStackHandler() {
		this(1);
	}

	public ItemStackHandler(int stacks) {
		this(emptyStackArray(stacks));
	}

	public ItemStackHandler(ItemStack[] stacks) {
		this.stacks = stacks;
		this.variants = new ItemVariant[stacks.length];
		this.lookup = new HashMap<>();
		for (int i = 0; i < stacks.length; i++) {
			ItemStack stack = stacks[i];
			this.variants[i] = ItemVariant.of(stack);
			getIndices(stack.getItem()).add(i);
		}
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		long inserted = 0;
		for (int i = 0; i < stacks.length; i++) {
			if (!isItemValid(i, resource))
				continue;
			ItemStack stack = stacks[i];
			if (!stack.isEmpty()) { // add to an existing stack
				inserted += insertToExistingStack(i, stack, resource, maxAmount - inserted, transaction);
			} else { // create a new stack
				inserted += insertToNewStack(i, resource, maxAmount - inserted, transaction);
			}
			if (maxAmount - inserted <= 0)
				break; // fully inserted
		}
		return inserted;
	}

	protected long insertToExistingStack(int index, ItemStack stack, ItemVariant resource, long maxAmount, TransactionContext ctx) {
		int space = getSpace(index, resource, stack);
		if (space <= 0)
			return 0; // no room? skip
		if (!resource.matches(stack))
			return 0; // can't stack? skip
		int toInsert = (int) Math.min(space, maxAmount);
		updateSnapshots(ctx);
		stack = ItemHandlerHelper.growCopy(stack, toInsert);
		contentsChangedInternal(index, stack, ctx);
		// no types were changed, only counts. Lookup is unchanged.
		return toInsert;
	}

	protected long insertToNewStack(int index, ItemVariant resource, long maxAmount, TransactionContext ctx) {
		int maxSize = getStackLimit(index, resource);
		int toInsert = (int) Math.min(maxSize, maxAmount);
		ItemStack stack = resource.toStack(toInsert);
		updateSnapshots(ctx);
		contentsChangedInternal(index, stack, ctx);
		return toInsert;
	}

	protected int getSpace(int index, ItemVariant resource, ItemStack stack) {
		int maxSize = getStackLimit(index, resource);
		int size = stack.getCount();
		return maxSize - size;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		Item item = resource.getItem();
		IntSortedSet indices = lookup.get(item);
		if (indices == null || indices.isEmpty())
			return 0; // no slots hold this item
		long extracted = 0;
		for (IntIterator itr = indices.intIterator(); itr.hasNext();) {
			int i = itr.nextInt();
			ItemStack stack = stacks[i];
			if (stack.hasTag() && !resource.matches(stack))
				continue; // nbt doesn't allow stacking? skip
			int size = stack.getCount();
			int toExtract = (int) Math.min(size, maxAmount - extracted);
			extracted += toExtract;
			int newSize = size - toExtract;
			boolean empty = newSize <= 0;
			stack = empty ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(stack, newSize);
			updateSnapshots(transaction);
			contentsChangedInternal(i, stack, transaction);
		}
		return extracted;
	}

	@Override
	@Nullable
	public StorageView<ItemVariant> exactView(TransactionContext transaction, ItemVariant resource) {
		StoragePreconditions.notBlank(resource);
		IntSortedSet indices = lookup.get(resource.getItem());
		if (indices == null || indices.isEmpty())
			return null;
		for (IntIterator itr = indices.intIterator(); itr.hasNext();) {
			int i = itr.nextInt();
			ItemStack stack = stacks[i];
			if (resource.matches(stack)) {
				return new ItemStackHandlerSlotView(this, i);
			}
		}
		return null;
	}

	protected void contentsChangedInternal(int slot, ItemStack newStack, @Nullable TransactionContext ctx) {
		ItemStack oldStack = stacks[slot];
		stacks[slot] = newStack;
		variants[slot] = ItemVariant.of(newStack);
		if (!oldStack.sameItem(newStack)) {
			// item changed, update the lookup
			updateLookup(oldStack.getItem(), newStack.getItem(), slot);
		}
		if (ctx != null) TransactionCallback.onSuccess(ctx, () -> onContentsChanged(slot));
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
		return new ItemStackHandlerIterator(this, transaction);
	}

	@Override
	protected SnapshotData createSnapshot() {
		return SnapshotData.of(this.stacks, this.variants, this.lookup);
	}

	@Override
	protected void readSnapshot(SnapshotData snapshot) {
		this.stacks = snapshot.stacks;
		this.variants = snapshot.variants;
		this.lookup = snapshot.lookup;
	}

	@Override
	public String toString() {
		return  getClass().getSimpleName() + '{' + "stacks=" + Arrays.toString(stacks) + ", variants=" + Arrays.toString(variants) + '}';
	}

	public int getSlots() {
		return stacks.length;
	}

	public void setStackInSlot(int slot, ItemStack stack) {
		contentsChangedInternal(slot, stack, null);
		onContentsChanged(slot);
	}

	/**
	 * This stack should never be modified.
	 */
	public ItemStack getStackInSlot(int slot) {
		return stacks[slot];
	}

	public ItemVariant getVariantInSlot(int slot) {
		return variants[slot];
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
		this.variants = new ItemVariant[size];
		for (int i = 0; i < this.stacks.length; i++) {
			stacks[i] = ItemStack.EMPTY;
			variants[i] = blank;
		}
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
		lookup.clear();
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < stacks.length) {
				ItemStack stack = ItemStack.of(itemTags);
				contentsChangedInternal(slot, stack, null);
			}
		}
		// fill in lookup with deserialized data
		for (int i = 0; i < stacks.length; i++) {
			ItemStack stack = stacks[i];
			getIndices(stack.getItem()).add(i);
		}
		onLoad();
	}

	protected void updateLookup(Item oldItem, Item newItem, int index) {
		getIndices(oldItem).remove(index);
		getIndices(newItem).add(index);
	}

	protected IntSortedSet getIndices(Item item) {
		return getIndices(lookup, item);
	}

	protected static IntSortedSet getIndices(Map<Item, IntSortedSet> lookup, Item item) {
		return lookup.computeIfAbsent(item, ItemStackHandler::makeSet);
	}

	protected static IntSortedSet makeSet(Item item) {
		return new IntAVLTreeSet(Integer::compareTo);
	}

	public static ItemStack[] emptyStackArray(int size) {
		ItemStack[] stacks = new ItemStack[size];
		Arrays.fill(stacks, ItemStack.EMPTY);
		return stacks;
	}

	public static class SnapshotData {
		public final ItemStack[] stacks;
		public final ItemVariant[] variants;
		public final Map<Item, IntSortedSet> lookup;

		@Deprecated(forRemoval = true)
		public SnapshotData(ItemStack[] stacks) {
			this.stacks = stacks;
			this.variants = new ItemVariant[stacks.length];
			this.lookup = new HashMap<>();
			for (int i = 0; i < stacks.length; i++) {
				ItemStack stack = stacks[i];
				variants[i] = ItemVariant.of(stack);
				getIndices(lookup, stack.getItem()).add(i);
			}
		}

		public SnapshotData(ItemStack[] stacks, ItemVariant[] variants, Map<Item, IntSortedSet> lookup) {
			this.stacks = stacks;
			this.variants = variants;
			this.lookup = lookup;
		}

		public static SnapshotData of(ItemStack[] stacks, ItemVariant[] variants, Map<Item, IntSortedSet> lookup) {
			ItemStack[] items = new ItemStack[stacks.length];
			System.arraycopy(stacks, 0, items, 0, stacks.length);
			ItemVariant[] vars = new ItemVariant[variants.length];
			System.arraycopy(variants, 0, vars, 0, variants.length);
			Map<Item, IntSortedSet> map = new HashMap<>();
			// a deep copy here seems unavoidable
			lookup.forEach((item, set) -> {
				IntSortedSet copy = makeSet(item);
				copy.addAll(set);
				map.put(item, copy);
			});
			return new SnapshotData(items, vars, map);
		}
	}

	@Override
	protected void onFinalCommit() {
		super.onFinalCommit();
	}
}
