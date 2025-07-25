package io.github.fabricators_of_create.porting_lib.transfer.item.wrapper;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.DebugMessages;
import net.fabricmc.fabric.impl.transfer.item.ItemVariantImpl;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;

/**
 * Wrapper around an {@link InventorySlotWrapper}, with additional canInsert and canExtract checks.
 */
class SidedInventorySlotWrapper implements SingleSlotStorage<ItemVariant> {
	private final InventorySlotWrapper slotWrapper;
	private final WorldlyContainer sidedInventory;
	private final Direction direction;

	SidedInventorySlotWrapper(InventorySlotWrapper slotWrapper, WorldlyContainer sidedInventory, Direction direction) {
		this.slotWrapper = slotWrapper;
		this.sidedInventory = sidedInventory;
		this.direction = direction;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!sidedInventory.canPlaceItemThroughFace(slotWrapper.slot, ((ItemVariantImpl) resource).getCachedStack(), direction)) {
			return 0;
		} else {
			return slotWrapper.insert(resource, maxAmount, transaction);
		}
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!sidedInventory.canTakeItemThroughFace(slotWrapper.slot, ((ItemVariantImpl) resource).getCachedStack(), direction)) {
			return 0;
		} else {
			return slotWrapper.extract(resource, maxAmount, transaction);
		}
	}

	@Override
	public boolean isResourceBlank() {
		return slotWrapper.isResourceBlank();
	}

	@Override
	public ItemVariant getResource() {
		return slotWrapper.getResource();
	}

	@Override
	public long getAmount() {
		return slotWrapper.getAmount();
	}

	@Override
	public long getCapacity() {
		return slotWrapper.getCapacity();
	}

	@Override
	public StorageView<ItemVariant> getUnderlyingView() {
		return slotWrapper.getUnderlyingView();
	}

	@Override
	public String toString() {
		return "SidedInventorySlotWrapper[%s#%d/%s]".formatted(DebugMessages.forInventory(sidedInventory), slotWrapper.slot, direction.getName());
	}
}
