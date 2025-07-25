package io.github.fabricators_of_create.porting_lib.transfer.item.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.impl.transfer.DebugMessages;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerInventoryStorage extends InventoryStorage {
	private final DroppedStacks droppedStacks;
	private final Inventory playerInventory;

	public PlayerInventoryStorage(Inventory playerInventory) {
		super(playerInventory);
		this.droppedStacks = new DroppedStacks();
		this.playerInventory = playerInventory;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return offer(resource, maxAmount, transaction);
	}

	public long offer(ItemVariant resource, long amount, TransactionContext tx) {
		StoragePreconditions.notBlankNotNegative(resource, amount);
		long initialAmount = amount;

		List<SingleSlotStorage<ItemVariant>> mainSlots = getSlots().subList(0, Inventory.INVENTORY_SIZE);

		// Stack into the main stack first and the offhand stack second.
		for (InteractionHand hand : InteractionHand.values()) {
			SingleSlotStorage<ItemVariant> handSlot = getHandSlot(hand);

			if (handSlot.getResource().equals(resource)) {
				amount -= handSlot.insert(resource, amount, tx);

				if (amount == 0) return initialAmount;
			}
		}

		// Otherwise insert into the main slots, stacking first.
		amount -= StorageUtil.insertStacking(mainSlots, resource, amount, tx);

		return initialAmount - amount;
	}

	public void drop(ItemVariant variant, long amount, boolean throwRandomly, boolean retainOwnership, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(variant, amount);

		// Drop in the world on the server side (will be synced by the game with the client).
		// Dropping items is server-side only because it involves randomness.
		if (amount > 0 && !playerInventory.player.level().isClientSide()) {
			droppedStacks.addDrop(variant, amount, throwRandomly, retainOwnership, transaction);
		}
	}

	public SingleSlotStorage<ItemVariant> getHandSlot(InteractionHand hand) {
		if (Objects.requireNonNull(hand) == InteractionHand.MAIN_HAND) {
			if (Inventory.isHotbarSlot(playerInventory.selected)) {
				return getSlot(playerInventory.selected);
			} else {
				throw new RuntimeException("Unexpected player selected slot: " + playerInventory.selected);
			}
		} else if (hand == InteractionHand.OFF_HAND) {
			return getSlot(Inventory.SLOT_OFFHAND);
		} else {
			throw new UnsupportedOperationException("Unknown hand: " + hand);
		}
	}

	@Override
	public String toString() {
		return "PlayerInventoryStorage[" + DebugMessages.forInventory(playerInventory) + "]";
	}

	private class DroppedStacks extends SnapshotParticipant<Integer> {
		final List<Entry> entries = new ArrayList<>();

		void addDrop(ItemVariant key, long amount, boolean throwRandomly, boolean retainOwnership, TransactionContext transaction) {
			updateSnapshots(transaction);
			entries.add(new Entry(key, amount, throwRandomly, retainOwnership));
		}

		@Override
		protected Integer createSnapshot() {
			return entries.size();
		}

		@Override
		protected void readSnapshot(Integer snapshot) {
			// effectively cancel dropping the stacks
			int previousSize = snapshot;

			while (entries.size() > previousSize) {
				entries.remove(entries.size() - 1);
			}
		}

		@Override
		protected void onFinalCommit() {
			// actually drop the stacks
			for (Entry entry : entries) {
				long remainder = entry.amount;

				while (remainder > 0) {
					int dropped = (int) Math.min(entry.key.getItem().getDefaultMaxStackSize(), remainder);
					playerInventory.player.drop(entry.key.toStack(dropped), entry.throwRandomly, entry.retainOwnership);
					remainder -= dropped;
				}
			}

			entries.clear();
		}

		private record Entry(ItemVariant key, long amount, boolean throwRandomly, boolean retainOwnership) {
		}
	}

	/**
	 * Throw items in the world from the player's location.
	 *
	 * <p>Note: This function has full transaction support, and will not actually drop the items until the outermost transaction is committed.
	 *
	 * @param variant The variant to drop.
	 * @param amount How many of the variant to drop.
	 * @param retainOwnership If true, set the {@code Thrower} NBT data to the player's UUID.
	 * @param transaction The transaction this operation is part of.
	 * @see Player#drop(ItemStack, boolean, boolean)
	 */
	public void drop(ItemVariant variant, long amount, boolean retainOwnership, TransactionContext transaction) {
		drop(variant, amount, false, retainOwnership, transaction);
	}

	/**
	 * Throw items in the world from the player's location.
	 *
	 * <p>Note: This function has full transaction support, and will not actually drop the items until the outermost transaction is committed.
	 *
	 * @param variant The variant to drop.
	 * @param amount How many of the variant to drop.
	 * @param transaction The transaction this operation is part of.
	 * @see Player#drop(ItemStack, boolean, boolean)
	 */
	public void drop(ItemVariant variant, long amount, TransactionContext transaction) {
		drop(variant, amount, false, transaction);
	}
}
