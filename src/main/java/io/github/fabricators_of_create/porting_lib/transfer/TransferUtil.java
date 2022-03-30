package io.github.fabricators_of_create.porting_lib.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.fabricators_of_create.porting_lib.transfer.cache.ClientFluidLookupCache;
import io.github.fabricators_of_create.porting_lib.transfer.cache.ClientItemLookupCache;
import io.github.fabricators_of_create.porting_lib.transfer.cache.EmptyFluidLookupCache;
import io.github.fabricators_of_create.porting_lib.transfer.cache.EmptyItemLookupCache;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemTransferable;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Utilities for transferring things.
 * for all storage getters, if a direction is not provided,
 * a CombinedStorage of all sides will be returned. All
 * may return null.
 */
public class TransferUtil {
	public static Transaction getTransaction() {
		TransactionContext open = Transaction.getCurrentUnsafe();
		if (open != null) {
			return open.openNested();
		}
		return Transaction.openOuter();
	}

	public static Storage<ItemVariant> getItemStorage(BlockEntity be) {
		return getItemStorage(be, null);
	}

	public static Storage<ItemVariant> getItemStorage(Level level, BlockPos pos) {
		return getItemStorage(level, pos, null);
	}

	public static Storage<ItemVariant> getItemStorage(Level level, BlockPos pos, @Nullable Direction direction) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be == null) return null;
		return getItemStorage(be, direction);
	}

	public static Storage<ItemVariant> getItemStorage(BlockEntity be, @Nullable Direction side) {
		boolean client = Objects.requireNonNull(be.getLevel()).isClientSide();
		// lib handling
		if (be instanceof ItemTransferable t && (!client || t.canTransferItemsClientSide())) return t.getItemStorage(side);
		else if (client) return null;
		// external handling
		List<Storage<ItemVariant>> itemStorages = new ArrayList<>();
		Level l = be.getLevel();
		BlockPos pos = be.getBlockPos();
		BlockState state = be.getBlockState();

		for (Direction direction : getDirections(side)) {
			Storage<ItemVariant> itemStorage = ItemStorage.SIDED.find(l, pos, state, be, direction);

			if (itemStorage != null) {
				if (itemStorages.size() == 0) {
					itemStorages.add(itemStorage);
					continue;
				}

				for (Storage<ItemVariant> storage : itemStorages) {
					if (!Objects.equals(itemStorage, storage)) {
						itemStorages.add(itemStorage);
						break;
					}
				}
			}
		}


		if (itemStorages.isEmpty()) return null;
		if (itemStorages.size() == 1) return itemStorages.get(0);
		return new CombinedStorage<>(itemStorages);
	}

	// Fluids

	public static Storage<FluidVariant> getFluidStorage(Level level, BlockPos pos) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be == null) return null;
		return getFluidStorage(be);
	}

	public static Storage<FluidVariant> getFluidStorage(BlockEntity be) {
		return getFluidStorage(be, null);
	}

	public static Storage<FluidVariant> getFluidStorage(BlockEntity be, @Nullable Direction side) {
		boolean client = Objects.requireNonNull(be.getLevel()).isClientSide();
		// lib handling
		if (be instanceof FluidTransferable t && (!client || t.canTransferFluidsClientSide())) return t.getFluidStorage(side);
		else if (client) return null;
		// external handling
		List<Storage<FluidVariant>> fluidStorages = new ArrayList<>();
		Level l = be.getLevel();
		BlockPos pos = be.getBlockPos();
		BlockState state = be.getBlockState();

		for (Direction direction : getDirections(side)) {
			Storage<FluidVariant> fluidStorage = FluidStorage.SIDED.find(l, pos, state, be, direction);

			if (fluidStorage != null) {
				if (fluidStorages.size() == 0) {
					fluidStorages.add(fluidStorage);
					continue;
				}

				for (Storage<FluidVariant> storage : fluidStorages) {
					if (!Objects.equals(fluidStorage, storage)) {
						fluidStorages.add(fluidStorage);
						break;
					}
				}
			}
		}

		if (fluidStorages.isEmpty()) return null;
		if (fluidStorages.size() == 1) return fluidStorages.get(0);
		return new CombinedStorage<>(fluidStorages);
	}

	@SuppressWarnings("unchecked")
	public static <T> Storage<T> getStorage(BlockEntity be, @Nullable Direction side, Class<T> capability) {
		if (capability == ItemVariant.class) {
			return (Storage<T>) getItemStorage(be, side);
		} else if (capability == FluidVariant.class) {
			return (Storage<T>) getFluidStorage(be, side);
		} else {
			throw new RuntimeException("Class must either be ItemVariant or FluidVariant!");
		}
	}

	private static Direction[] getDirections(@Nullable Direction direction) {
		if (direction == null) return Direction.values();
		return new Direction[] { direction };
	}

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

	public static <T> long firstCapacity(Storage<T> storage) {
		List<Long> capacities = capacities(storage, 1);
		return capacities.size() > 0 ? capacities.get(0) : 0;
	}

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
			for (StorageView<T> view : storage.iterable(t)) {
				capacities.add(view.getCapacity());
				if (capacities.size() == cutoff)
					break;
			}
		}
		return capacities;
	}

	public static FluidStack firstCopyOrEmpty(Storage<FluidVariant> storage) {
		return firstOrEmpty(storage).copy();
	}

	public static FluidStack firstOrEmpty(Storage<FluidVariant> storage) {
		FluidStack stack = getFirstFluid(storage);
		return stack == null ? FluidStack.EMPTY : stack;
	}

	@Nullable
	public static FluidStack getFirstFluid(Storage<FluidVariant> storage) {
		List<FluidStack> stacks = getFluids(storage, 1);
		if (stacks.size() > 0) return stacks.get(0);
		return null;
	}

	public static List<FluidStack> getAllFluids(Storage<FluidVariant> storage) {
		return getFluids(storage, Integer.MAX_VALUE);
	}

	/**
	 * Find all fluids inside a storage.
	 * @param cutoff number of fluids to find before exiting early
	 */
	public static List<FluidStack> getFluids(Storage<FluidVariant> storage, int cutoff) {
		List<FluidStack> stacks = new ArrayList<>();
		try (Transaction t = getTransaction()) {
			for (StorageView<FluidVariant> view : storage.iterable(t)) {
				if (!view.isResourceBlank()) {
					stacks.add(new FluidStack(view));
				}
				if (stacks.size() == cutoff) {
					break;
				}
			}
		}
		return stacks;
	}

	public static List<ItemStack> getAllItems(Storage<ItemVariant> storage) {
		return getItems(storage, Integer.MAX_VALUE);
	}

	public static List<ItemStack> getItems(Storage<ItemVariant> storage, int cutoff) {
		List<ItemStack> stacks = new ArrayList<>();
		try (Transaction t = getTransaction()) {
			for (StorageView<ItemVariant> view : storage.iterable(t)) {
				if (!view.isResourceBlank()) {
					long contained = view.getAmount();
					ItemVariant item = view.getResource();
					int maxSize = item.getItem().getMaxStackSize();
					while (contained > 0 && stacks.size() < cutoff) {
						int stackSize = Math.min(maxSize, (int) contained);
						contained -= stackSize;
						stacks.add(item.toStack(stackSize));
					}

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
		boolean success = true;
		try (Transaction t = getTransaction()) {
			for (StorageView<T> view : storage.iterable(t)) {
				long toRemove = view.getAmount();
				long actual = view.extract(view.getResource(), view.getAmount(), t);
				success &= toRemove == actual;
			}
			t.commit();
		}
		return success;
	}

	public static FluidStack extractAnyFluid(Storage<FluidVariant> storage, long maxAmount) {
		FluidStack fluid = FluidStack.EMPTY;
		try (Transaction t = getTransaction()) {
			for (StorageView<FluidVariant> view : storage.iterable(t)) {
				if (!view.isResourceBlank()) {
					long amount = Math.min(maxAmount, view.getAmount());
					long extracted = view.extract(view.getResource(), amount, t);
					maxAmount -= extracted;
					if (fluid.isEmpty()) {
						fluid = new FluidStack(view.getResource(), extracted);
					} else if (fluid.canFill(view.getResource())) {
						fluid.grow(extracted);
					}
					if (maxAmount == 0)
						break;
				}
			}
			t.commit();
			return fluid;
		}
	}

	public static ItemStack extractAnyItem(Storage<ItemVariant> storage, long maxAmount) {
		ItemStack stack = ItemStack.EMPTY;
		try (Transaction t = getTransaction()) {
			for (StorageView<ItemVariant> view : storage.iterable(t)) {
				if (!view.isResourceBlank()) {
					long amount = Math.min(view.getResource().getItem().getMaxStackSize(), Math.min(maxAmount, view.getAmount()));
					long extracted = view.extract(view.getResource(), amount, t);
					maxAmount -= extracted;
					if (stack.isEmpty()) {
						stack = view.getResource().toStack((int) extracted);
					} else if (view.getResource().matches(stack)) {
						stack.grow((int) extracted);
					}
					if (maxAmount == 0)
						break;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public static <T> long extract(Storage<T> storage, T variant, long amount) {
		try (Transaction t = getTransaction()) {
			long extracted = storage.extract(variant, amount, t);
			t.commit();
			return extracted;
		}
	}

	public static long extractItem(Storage<ItemVariant> storage, ItemStack stack) {
		return extract(storage, ItemVariant.of(stack), stack.getCount());
	}

	public static long extractFluid(Storage<FluidVariant> storage, FluidStack stack) {
		return extract(storage, stack.getType(), stack.getAmount());
	}

	public static <T> long insert(Storage<T> storage, T variant, long amount) {
		try (Transaction t = getTransaction()) {
			long inserted = storage.insert(variant, amount, t);
			t.commit();
			return inserted;
		}
	}

	public static long insertItem(Storage<ItemVariant> storage, ItemStack stack) {
		return insert(storage, ItemVariant.of(stack), stack.getCount());
	}

	public static long insertFluid(Storage<FluidVariant> storage, FluidStack stack) {
		return insert(storage, stack.getType(), stack.getAmount());
	}

	public static long insertToNotHotbar(Player player, ItemVariant variant, long amount) {
		long inserted = 0;
		try (Transaction t = getTransaction()) {
			PlayerInventoryStorage inv = PlayerInventoryStorage.of(player);
			List<SingleSlotStorage<ItemVariant>> slots = inv.getSlots();
			for (int i = 9; i < slots.size(); i++) { // start at 9 and skip hotbar
				SingleSlotStorage<ItemVariant> slot = slots.get(i);
				inserted += slot.insert(variant, amount - inserted, t);
				if (amount == 0) break;
			}
			t.commit();
			return inserted;
		}
	}

	public static BlockApiCache<Storage<ItemVariant>, Direction> getItemCache(Level level, BlockPos pos) {
		if (level instanceof ServerLevel server) {
			return BlockApiCache.create(ItemStorage.SIDED, server, pos);
		} else if (level instanceof ClientLevel client) {
			return new ClientItemLookupCache(client, pos);
		}
		return EmptyItemLookupCache.INSTANCE;
	}

	public static BlockApiCache<Storage<FluidVariant>, Direction> getFluidCache(Level level, BlockPos pos) {
		if (level instanceof ServerLevel server) {
			return BlockApiCache.create(FluidStorage.SIDED, server, pos);
		} else if (level instanceof ClientLevel client) {
			return new ClientFluidLookupCache(client, pos);
		}
		return EmptyFluidLookupCache.INSTANCE;
	}

	public static void initApi() {
		FluidStorage.SIDED.registerFallback((world, pos, state, be, face) -> {
			if (be instanceof FluidTransferable t) {
				return t.getFluidStorage(face);
			}
			return null;
		});
		ItemStorage.SIDED.registerFallback((world, pos, state, be, face) -> {
			if (be instanceof ItemTransferable t) {
				return t.getItemStorage(face);
			}
			return null;
		});
	}
}
