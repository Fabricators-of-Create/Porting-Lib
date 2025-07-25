package io.github.fabricators_of_create.porting_lib.transfer.item.wrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.MapMaker;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.impl.transfer.DebugMessages;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;

/**
 * Implementation of {@link InventoryStorage}.
 * Note on thread-safety: we assume that Inventory's are inherently single-threaded, and no attempt is made at synchronization.
 * However, the access to implementations can happen on multiple threads concurrently, which is why we use a thread-safe wrapper map.
 */
public class InventoryStorage extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements IInventoryStorage {
	/**
	 * Global wrapper concurrent map.
	 *
	 * <p>A note on GC: weak keys alone are not suitable as the InventoryStorage slots strongly reference the Inventory keys.
	 * Weak values are suitable, but we have to ensure that the InventoryStorageImpl remains strongly reachable as long as
	 * one of the slot wrappers refers to it, hence the {@code strongRef} field in {@link InventorySlotWrapper}.
	 */
	// TODO: look into promoting the weak reference to a soft reference if building the wrappers becomes a performance bottleneck.
	// TODO: should have identity semantics?
	private static final Map<Container, InventoryStorage> WRAPPERS = new MapMaker().weakValues().makeMap();

	public static IInventoryStorage of(Container inventory, @Nullable Direction direction) {
		InventoryStorage storage = WRAPPERS.computeIfAbsent(inventory, inv -> {
			if (inv instanceof Inventory playerInventory) {
				return new PlayerInventoryStorage(playerInventory);
			} else {
				return new InventoryStorage(inv);
			}
		});
		storage.resizeSlotList();
		return storage.getSidedWrapper(direction);
	}

	final Container inventory;
	/**
	 * This {@code backingList} is the real list of wrappers.
	 * The {@code parts} in the superclass is the public-facing unmodifiable sublist with exactly the right amount of slots.
	 */
	final List<InventorySlotWrapper> backingList;
	/**
	 * This participant ensures that markDirty is only called once for the entire inventory.
	 */
	final MarkDirtyParticipant markDirtyParticipant = new MarkDirtyParticipant();

	InventoryStorage(Container inventory) {
		super(Collections.emptyList());
		this.inventory = inventory;
		this.backingList = new ArrayList<>();
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		return parts;
	}

	/**
	 * Resize slot list to match the current size of the inventory.
	 */
	private void resizeSlotList() {
		int inventorySize = inventory.getContainerSize();

		// If the public-facing list must change...
		if (inventorySize != parts.size()) {
			// Ensure we have enough wrappers in the backing list.
			while (backingList.size() < inventorySize) {
				backingList.add(new InventorySlotWrapper(this, backingList.size()));
			}

			// Update the public-facing list.
			parts = Collections.unmodifiableList(backingList.subList(0, inventorySize));
		}
	}

	private IInventoryStorage getSidedWrapper(@Nullable Direction direction) {
		if (inventory instanceof WorldlyContainer && direction != null) {
			return new SidedInventoryStorage(this, direction);
		} else {
			return this;
		}
	}

	@Override
	public String toString() {
		return "InventoryStorage[" + DebugMessages.forInventory(inventory) + "]";
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getItem(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		inventory.setItem(slot, stack);
	}

	@Override
	public int getSlotLimit(int slot) {
		return inventory.getItem(slot).getMaxStackSize();
	}

	// Boolean is used to prevent allocation. Null values are not allowed by SnapshotParticipant.
	class MarkDirtyParticipant extends SnapshotParticipant<Boolean> {
		@Override
		protected Boolean createSnapshot() {
			return Boolean.TRUE;
		}

		@Override
		protected void readSnapshot(Boolean snapshot) {
		}

		@Override
		protected void onFinalCommit() {
			inventory.setChanged();
		}
	}
}
