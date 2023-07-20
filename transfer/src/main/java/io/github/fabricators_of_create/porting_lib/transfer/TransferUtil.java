package io.github.fabricators_of_create.porting_lib.transfer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

import net.minecraft.world.item.Items;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Utilities for transferring things.
 * All Storage-modifying helpers use {@link TransferUtil#getTransaction()} to
 * create Transactions, and they automatically commit.
 * This means that in order to simulate these actions, they must be wrapped as so:<pre>{@code
 *  try (Transaction t = Transaction.openOuter()) {
 * 		boolean result = TransferUtil.clearStorage(storage);
 * 		t.abort();
 * 		if (result) {
 * 		 	...
 * 		}
 * 	}
 * }</pre>
 */
@SuppressWarnings({"removal", "unused"})
public class TransferUtil {
	/**
	 * @return Either an outer transaction or a nested one in the current open one
	 */
	public static Transaction getTransaction() {
		if (Transaction.isOpen()) {
			//noinspection deprecation
			TransactionContext open = Transaction.getCurrentUnsafe();
			if (open != null) {
				return open.openNested();
			}
		}
		return Transaction.openOuter();
	}

	// Item Storage getting

	@Nullable
	public static Storage<ItemVariant> getItemStorage(Level level, BlockPos pos, @Nullable Direction side) {
		return getItemStorage(level, pos, null, side);
	}

	@Nullable
	public static Storage<ItemVariant> getItemStorage(Level level, BlockPos pos) {
		return getItemStorage(level, pos, null);
	}

	/**
	 * Be careful with this method. Don't forget that non-BEs can also hold storages.
	 */
	@Nullable
	public static Storage<ItemVariant> getItemStorage(BlockEntity be, @Nullable Direction side) {
		return getItemStorage(null, null, be, side);
	}

	/**
	 * Be careful with this method. Don't forget that non-BEs can also hold storages.
	 */
	@Nullable
	public static Storage<ItemVariant> getItemStorage(BlockEntity be) {
		return getItemStorage(be, null);
	}

	/**
	 * Using the provided Level and BlockPos OR BlockEntity, find a Storage containing ItemVariants.
	 * Prefer {@link TransferUtil#getItemStorage(Level, BlockPos, Direction)} variant generally.
	 * @param level the Level to check in - may be client only, despite what regular FAPI allows. Null is allowed as long as 'be' is NOT null.
	 * @param pos the position to check. Null is allowed as long as 'be' is NOT null.
	 * @param be the Block Entity to check. Null is allowed as long as 'level' and 'pos' are NOT null.
	 * @param side the Direction to check in - null is considered all sides and will return a wrapper around all found.
	 * @see TransferUtil#getItemStorage(Level, BlockPos, Direction)
	 * @return a Storage of ItemVariants, or null if none found.
	 */
	@Nullable
	public static Storage<ItemVariant> getItemStorage(Level level, BlockPos pos, BlockEntity be, @Nullable Direction side) {
		if (be == null) {
			Objects.requireNonNull(level, "If a null Block Entity is provided, the Level may NOT be null!");
			Objects.requireNonNull(pos, "If a null Block Entity is provided, the pos may NOT be null!");
			be = level.getBlockEntity(pos);
		}
		if (level == null || pos == null) {
			Objects.requireNonNull(be, "If a null level or pos is provided, the Block Entity may NOT be null!");
			level = be.getLevel();
			pos = be.getBlockPos();
		}
		if (level == null)
			return null;
		BlockState state = be == null ? level.getBlockState(pos) : be.getBlockState();

		return ItemStorage.SIDED.find(level, pos, state, be, side);
	}

	// Fluid storage getting

	@Nullable
	public static Storage<FluidVariant> getFluidStorage(Level level, BlockPos pos, @Nullable Direction side) {
		return getFluidStorage(level, pos, null, side);
	}

	@Nullable
	public static Storage<FluidVariant> getFluidStorage(Level level, BlockPos pos) {
		return getFluidStorage(level, pos, null);
	}


	/**
	 * Be careful with this method. Don't forget that non-BEs can also hold storages.
	 */
	@Nullable
	public static Storage<FluidVariant> getFluidStorage(BlockEntity be, @Nullable Direction side) {
		return getFluidStorage(null, null, be, side);
	}

	/**
	 * Be careful with this method. Don't forget that non-BEs can also hold storages.
	 */
	@Nullable
	public static Storage<FluidVariant> getFluidStorage(BlockEntity be) {
		return getFluidStorage(be, null);
	}

	/**
	 * Using the provided Level and BlockPos OR BlockEntity, find a Storage containing FluidVariants.
	 * Prefer {@link TransferUtil#getFluidStorage(Level, BlockPos, Direction)} variant generally.
	 * @param level the Level to check in - may be client only, despite what regular FAPI allows. Null is allowed as long as 'be' is NOT null.
	 * @param pos the position to check. Null is allowed as long as 'be' is NOT null.
	 * @param be the Block Entity to check. Null is allowed as long as 'level' and 'pos' are NOT null.
	 * @param side the Direction to check in - null is considered all sides and will return a wrapper around all found.
	 * @see TransferUtil#getFluidStorage(Level, BlockPos, Direction)
	 * @return a Storage of FluidVariants, or null if none found.
	 */
	@Nullable
	public static Storage<FluidVariant> getFluidStorage(Level level, BlockPos pos, BlockEntity be, @Nullable Direction side) {
		if (be == null) {
			Objects.requireNonNull(level, "If a null Block Entity is provided, the Level may NOT be null!");
			Objects.requireNonNull(pos, "If a null Block Entity is provided, the pos may NOT be null!");
			be = level.getBlockEntity(pos);
		}
		if (level == null || pos == null) {
			Objects.requireNonNull(be, "If a null level or pos is provided, the Block Entity may NOT be null!");
			level = be.getLevel();
			pos = be.getBlockPos();
		}
		if (level == null)
			return null;
		BlockState state = be == null ? level.getBlockState(pos) : be.getBlockState();
		return FluidStorage.SIDED.find(level, pos, state, be, side);
	}

	// misc utils below

	/**
	 * Find A Storage of the given Class using the given BlockEntity and Direction.
	 * @param be the BlockEntity to check
	 * @param side the Direction to check
	 * @param capability either ItemVariant.class or FluidVariant.class
	 * @param <T> either ItemVariant or FluidVariant, corresponding to the provided Class.
	 * @return the found storage, or null if none available.
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> Storage<T> getStorage(BlockEntity be, @Nullable Direction side, Class<T> capability) {
		if (capability == ItemVariant.class) {
			return (Storage<T>) getItemStorage(null, null, be, side);
		} else if (capability == FluidVariant.class) {
			return (Storage<T>) getFluidStorage(null, null, be, side);
		} else {
			throw new RuntimeException("Class must either be ItemVariant or FluidVariant!");
		}
	}

	/**
	 * @return an Optional of a FluidStack containing the first fluid found in the supplied item, or Optional.empty() if none.
	 */
	public static Optional<FluidStack> getFluidContained(ItemStack container) {
		if (container != null && !container.isEmpty()) {
			Storage<FluidVariant> storage = ContainerItemContext.withInitial(container).find(FluidStorage.ITEM);
			if (storage != null) {
				FluidStack first = getFirstFluid(storage);
				if (first != null) return Optional.of(first);
			}
		}
		return Optional.empty();
	}

	/**
	 * @return the capacity of the first StorageView of the storage
	 */
	public static <T> long firstCapacity(Storage<T> storage) {
		List<Long> capacities = capacities(storage, 1);
		return capacities.size() > 0 ? capacities.get(0) : 0;
	}

	/**
	 * @return the capacities of all StorageViews of the storage added together.
	 */
	public static <T> long totalCapacity(Storage<T> storage) {
		long total = 0;
		List<Long> capacities = capacities(storage, Integer.MAX_VALUE);
		for (Long l : capacities) total += l;
		return total;
	}

	/**
	 * Finds the capacities of each StorageView in the storage.
	 * @param cutoff number of capacities to find before exiting early
	 */
	public static <T> List<Long> capacities(Storage<T> storage, int cutoff) {
		List<Long> capacities = new ArrayList<>();
		try (Transaction t = getTransaction()) {
			for (StorageView<T> view : storage) {
				capacities.add(view.getCapacity());
				if (capacities.size() == cutoff)
					break;
			}
		}
		return capacities;
	}

	/** @return a copy of the first FluidStack found, or EMPTY if none */
	public static FluidStack firstCopyOrEmpty(Storage<FluidVariant> storage) {
		return firstOrEmpty(storage).copy();
	}

	/** @return the first FluidStack found, or EMPTY if none */
	public static FluidStack firstOrEmpty(Storage<FluidVariant> storage) {
		FluidStack stack = getFirstFluid(storage);
		return stack == null ? FluidStack.EMPTY : stack;
	}

	/** @return the first FluidStack found, or null if none */
	@Nullable
	public static FluidStack getFirstFluid(Storage<FluidVariant> storage) {
		List<FluidStack> stacks = getFluids(storage, 1);
		if (stacks.size() > 0) return stacks.get(0);
		return null;
	}

	/** @see TransferUtil#getFluids(Storage, int) */
	public static List<FluidStack> getAllFluids(Storage<FluidVariant> storage) {
		return getFluids(storage, Integer.MAX_VALUE);
	}

	/**
	 * Find all fluids inside a storage, until the cutoff is hit. One FluidStack for each non-empty StorageView.
	 * @param cutoff number of fluids to find before exiting early
	 */
	public static List<FluidStack> getFluids(Storage<FluidVariant> storage, int cutoff) {
		List<FluidStack> stacks = new ArrayList<>();
		try (Transaction t = getTransaction()) {
			for (StorageView<FluidVariant> view : storage.nonEmptyViews()) {
				stacks.add(new FluidStack(view));
				if (stacks.size() == cutoff) {
					break;
				}
			}
		}
		return stacks;
	}

	/** @see TransferUtil#getItems(Storage, int) */
	public static List<ItemStack> getAllItems(Storage<ItemVariant> storage) {
		return getItems(storage, Integer.MAX_VALUE);
	}

	/**
	 * Find all ItemStacks within a Storage. There is no guarantee a single StorageView holds a single ItemStack.
	 * For example, if a single StorageView holds 128 dirt blocks, that will be converted into two ItemStacks of 64.
	 * @return a list of all ItemStacks found
	 */
	public static List<ItemStack> getItems(Storage<ItemVariant> storage, int cutoff) {
		List<ItemStack> stacks = new ArrayList<>();
		try (Transaction t = getTransaction()) {
			for (StorageView<ItemVariant> view : storage.nonEmptyViews()) {
				long contained = view.getAmount();
				ItemVariant item = view.getResource();
				int maxSize = item.getItem().getMaxStackSize();
				while (contained > 0 && stacks.size() < cutoff) {
					int stackSize = Math.min(maxSize, (int) contained);
					contained -= stackSize;
					stacks.add(item.toStack(stackSize));
				}
				if (stacks.size() == cutoff) {
					break;
				}
			}
		}
		return stacks;
	}

	/**
	 * Remove as much as possible from a Storage.
	 * @return true if all removed
	 */
	public static <T> boolean clearStorage(Storage<T> storage) {
		if (!storage.supportsExtraction()) {
			return false;
		}
		boolean success = true;
		try (Transaction t = getTransaction()) {
			for (StorageView<T> view : storage.nonEmptyViews()) {
				long amount = view.getAmount();
				long extracted = view.extract(view.getResource(), amount, t);
				if (extracted < amount)
					success = false;
			}
			t.commit();
		}
		return success;
	}

	/**
	 * Try to extract any FluidStack possible from a Storage.
	 * @return the extracted FluidStack, or EMPTY if none.
	 */
	public static FluidStack extractAnyFluid(Storage<FluidVariant> storage, long maxAmount, Transaction tx) {
		if (!storage.supportsExtraction())
			return FluidStack.EMPTY;
		ResourceAmount<FluidVariant> extracted = StorageUtil.extractAny(storage, maxAmount, tx);
		return extracted == null ? FluidStack.EMPTY : new FluidStack(extracted);
	}

	/**
	 * Try to extract any FluidStack possible from a Storage.
	 * @return the extracted FluidStack, or EMPTY if none.
	 */
	public static FluidStack extractAnyFluid(Storage<FluidVariant> storage, long maxAmount) {
		try (Transaction tx = getTransaction()) {
			FluidStack fluid = extractAnyFluid(storage, maxAmount, tx);
			tx.commit();
			return fluid;
		}
	}

	/**
	 * Try to extract any FluidStack possible from a Storage without affecting the actual storage contents.
	 * @return the extracted FluidStack, or EMPTY if none.
	 */
	public static FluidStack simulateExtractAnyFluid(Storage<FluidVariant> storage, long maxAmount) {
		try (Transaction t = getTransaction()) {
			return extractAnyFluid(storage, maxAmount, t);
		}
	}

	/** @see TransferUtil#extractAnyFluid(Storage, long) */
	public static ItemStack extractAnyItem(Storage<ItemVariant> storage, long maxAmount, Transaction tx) {
		if (!storage.supportsExtraction())
			return ItemStack.EMPTY;
		int max = (int) Math.min(Integer.MAX_VALUE, maxAmount);
		ResourceAmount<ItemVariant> extracted = StorageUtil.extractAny(storage, max, tx);
		return extracted == null ? ItemStack.EMPTY : extracted.resource().toStack((int) extracted.amount());
	}

	/** @see TransferUtil#extractAnyFluid(Storage, long) */
	public static ItemStack extractAnyItem(Storage<ItemVariant> storage, long maxAmount) {
		try (Transaction tx = getTransaction()) {
			ItemStack stack = extractAnyItem(storage, maxAmount, tx);
			tx.commit();
			return stack;
		}
	}

	/** @see TransferUtil#simulateExtractAnyFluid(Storage, long) (Storage, long) */
	public static ItemStack simulateExtractAnyItem(Storage<ItemVariant> storage, long maxAmount) {
		try (Transaction tx = getTransaction()) {
			return extractAnyItem(storage, maxAmount, tx);
		}
	}

	/** Less clunky way of simulating extraction on a {@link StorageView<T>} */
	public static <T> long simulateExtractView(@NotNull StorageView<T> view, T variant, long amount) {
		try (Transaction t = getTransaction()) {
			return view.extract(variant, amount, t);
		}
	}

	/** Quickly extract and commit the given variant with the given amount. */
	public static <T> long extract(Storage<T> storage, T variant, long amount) {
		if (!storage.supportsExtraction()) return 0;
		try (Transaction t = getTransaction()) {
			long extracted = storage.extract(variant, amount, t);
			t.commit();
			return extracted;
		}
	}

	/** Quickly extract and commit the given ItemStack. */
	public static long extractItem(Storage<ItemVariant> storage, ItemStack stack) {
		return extract(storage, ItemVariant.of(stack), stack.getCount());
	}

	/** Quickly extract and commit the given ItemStack. */
	public static long extractFluid(Storage<FluidVariant> storage, FluidStack stack) {
		return extract(storage, stack.getType(), stack.getAmount());
	}

	/** Quickly insert and commit the given variant with the given amount. */
	public static <T> long insert(Storage<T> storage, T variant, long amount) {
		if (!storage.supportsInsertion()) return 0;
		try (Transaction t = getTransaction()) {
			long inserted = storage.insert(variant, amount, t);
			t.commit();
			return inserted;
		}
	}

	/** Quickly insert and commit the given ItemStack. */
	public static long insertItem(Storage<ItemVariant> storage, ItemStack stack) {
		return insert(storage, ItemVariant.of(stack), stack.getCount());
	}

	/** Quickly insert and commit the given FluidStack. */
	public static long insertFluid(Storage<FluidVariant> storage, FluidStack stack) {
		return insert(storage, stack.getType(), stack.getAmount());
	}

	/** Insert the given variant and amount into the given Player's inventory, excluding the hotbar, offhand, and armor. */
	public static long insertToMainInv(Player player, ItemVariant variant, long amount) {
		long inserted = 0;
		try (Transaction t = getTransaction()) {
			PlayerInventoryStorage inv = PlayerInventoryStorage.of(player);
			if (!inv.supportsInsertion()) return 0;
			List<SingleSlotStorage<ItemVariant>> slots = inv.getSlots();
			for (int i = 9; i < Inventory.INVENTORY_SIZE; i++) { // start at 9 and skip hotbar, end before armor and offhand
				SingleSlotStorage<ItemVariant> slot = slots.get(i);
				inserted += slot.insert(variant, amount - inserted, t);
				if (amount == 0) break;
			}
			t.commit();
			return inserted;
		}
	}

	/**
	 * Extract all contents of the Storage as ItemStacks.
	 * Follows the same logic as {@link TransferUtil#getItems(Storage, int) }
	 */
	public static List<ItemStack> extractAllAsStacks(Storage<ItemVariant> storage) {
		List<ItemStack> stacks = new ArrayList<>();
		if (!storage.supportsExtraction()) return stacks;
		try (Transaction t = getTransaction()) {
			Iterator<? extends StorageView<ItemVariant>> itr = storage.nonEmptyIterator();
			StorageView<ItemVariant> currentView = itr.hasNext() ? itr.next() : null;
			while (currentView != null) {
				long contained = currentView.getAmount();
				if (contained == 0) {
					currentView = itr.hasNext() ? itr.next() : null;
					continue;
				}
				ItemVariant variant = currentView.getResource();
				int max = (int) Math.min(contained, variant.getItem().getMaxStackSize());
				long extracted = currentView.extract(variant, max, t);
				if (extracted == 0) {
					currentView = itr.hasNext() ? itr.next() : null;
					continue;
				}
				ItemStack stack = variant.toStack((int) extracted);
				stacks.add(stack);
			}
			t.commit();
			return stacks;
		}
	}


	/**
	 * Gets the filled bucket for the specified fluid.
	 * @param variant contents used to fill the bucket. {@link FluidVariant} is used instead of Fluid to preserve fluid NBT.
	 * @return the filled bucket.
	 */
	public static ItemStack getFilledBucket(FluidVariant variant) {
		ContainerItemContext context = ContainerItemContext.withInitial(Items.BUCKET.getDefaultInstance());
		try (Transaction tx = TransferUtil.getTransaction()) {
			Storage<FluidVariant> storage = context.find(FluidStorage.ITEM);
			if (storage != null) {
				storage.insert(variant, FluidConstants.BUCKET, tx);
				tx.commit();
			}
		}
		return context.getItemVariant().toStack();
	}

	/**
	 * Extract anything matching the given predicate, or null if none available.
	 */
	@Nullable
	public static <T extends TransferVariant<?>> ResourceAmount<T> extractMatching(Storage<T> storage, Predicate<T> predicate,
																				   long maxAmount, @Nullable TransactionContext ctx) {
		T variant = null;
		for (StorageView<T> view : storage.nonEmptyViews()) {
			T resource = view.getResource();
			if (predicate.test(resource)) {
				variant = resource;
				break;
			}
		}
		if (variant == null || variant.isBlank())
			return null;
		try (Transaction t = Transaction.openNested(ctx)) {
			long extracted = storage.extract(variant, maxAmount, t);
			if (extracted == 0)
				return null;
			return new ResourceAmount<>(variant, extracted);
		}
	}

	/**
	 * Restrict a long to the integer range and avoid overflow from casting.
	 */
	public static int truncateLong(long l) {
		if (l > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		} else if (l < Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		}
		return (int) l;
	}
}
