package io.github.fabricators_of_create.porting_lib.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Utilities for transfering things.
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
		// client handling
		if (Objects.requireNonNull(be.getLevel()).isClientSide()) {
			return null;
		}
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
		// client handling
		if (client) { // TODO CLIENT TRANSFER
//			IFluidStorage cached = FluidTileDataStorage.getCachedStorage(be);
//			return LazyOptional.ofObject(cached);
			return null;
		}
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

	private static Direction[] getDirections(@Nullable Direction direction) {
		if (direction == null) return Direction.values();
		return new Direction[] { direction };
	}

	public static Optional<FluidStack> getFluidContained(ItemStack container) {
		if (container != null && !container.isEmpty()) {
			Storage<FluidVariant> storage = FluidStorage.ITEM.find(container, ContainerItemContext.withInitial(container));
			if (storage != null) {
				FluidStack first = getFirstFluid(storage);
				if (first != null) return Optional.of(first);
			}
		}
		return Optional.empty();
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
	 * Find all unique fluids inside a storage.
	 * @param cutoff number of unique fluids to find before exiting early
	 */
	public static List<FluidStack> getFluids(Storage<FluidVariant> storage, int cutoff) {
		List<FluidStack> stacks = new ArrayList<>();
		try (Transaction t = getTransaction()) {
			for (StorageView<FluidVariant> view : storage.iterable(t)) {
				if (!view.isResourceBlank()) {
					// get the contained stack and only add to list if unique
					FluidStack contained = new FluidStack(view.getResource(), view.getAmount());
					if (stacks.size() == 0) {
						stacks.add(contained);
						continue;
					} else {
						// check if unique
						FluidStack existing = null;
						for (FluidStack stack : stacks) {
							if (stack.isFluidEqual(contained)) {
								// find the existing matching stack and exit early
								existing = stack;
								break;
							}
						}
						if (existing == null) {
							stacks.add(contained); // only add if unique
						} else { // else just update amount stored
							long newAmount = existing.getAmount() + contained.getAmount();
							existing.setAmount(newAmount);
						}
					}
				}
				if (stacks.size() == cutoff) {
					break;
				}
			}
		}
		return stacks;
	}
}
