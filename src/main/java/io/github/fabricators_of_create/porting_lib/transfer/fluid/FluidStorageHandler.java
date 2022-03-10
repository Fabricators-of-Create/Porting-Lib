package io.github.fabricators_of_create.porting_lib.transfer.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps a Storage in an IFluidHandler, for use in Create
 */
@SuppressWarnings({"UnstableApiUsage"})
public class FluidStorageHandler implements IFluidHandler {
	protected final Storage<FluidVariant> storage;
	protected long version;
	protected int tanks;
	protected FluidStack[] stacks;
	protected Long[] capacities;

	public FluidStorageHandler(Storage<FluidVariant> storage) {
		this.storage = storage;
		this.version = storage.getVersion();
		updateContents();
	}

	public boolean shouldUpdate() {
		return storage.getVersion() != version;
	}

	private void updateContents() {
		List<FluidStack> stacks = new ArrayList<>();
		List<Long> capacities = new ArrayList<>();
		try (Transaction t = Transaction.openOuter()) {
			for (StorageView<FluidVariant> view : storage.iterable(t)) {
				stacks.add(new FluidStack(view.getResource(), view.getAmount()));
				capacities.add(view.getCapacity());
			}
			t.abort();
		}
		this.stacks = stacks.toArray(FluidStack[]::new);
		this.capacities = capacities.toArray(Long[]::new);
		this.tanks = stacks.size();
		this.version = storage.getVersion();
	}

	private boolean validIndex(int tank) {
		return tank >= 0 && tank < tanks;
	}

	@Override
	public int getTanks() {
		if (shouldUpdate())
			updateContents();
		return tanks;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		if (validIndex(tank)) {
			if (shouldUpdate())
				updateContents();
			return stacks[tank].copy();
		}
		return FluidStack.EMPTY;
	}

	@Override
	public long getTankCapacity(int tank) {
		if (validIndex(tank)) {
			if (shouldUpdate())
				updateContents();
			return (int) (long) capacities[tank];
		}
		return 0;
	}

	@Override
	public long fill(FluidStack stack, boolean sim) {
		if (stack.isEmpty())
			return 0;
		if (!storage.supportsInsertion())
			return 0;

		try (Transaction t = Transaction.openOuter()) {
			long filled = storage.insert(stack.getType(), stack.getAmount(), t);
			if (!sim) {
				t.commit();
				if (shouldUpdate())
					updateContents();
			}
			return filled;
		}
	}

	@Override
	public FluidStack drain(FluidStack stack, boolean sim) {
		if (stack.isEmpty())
			return FluidStack.EMPTY;
		if (!storage.supportsExtraction())
			return FluidStack.EMPTY;

		try (Transaction t = Transaction.openOuter()) {
			long extracted = storage.extract(stack.getType(), stack.getAmount(), t);
			if (!sim) {
				t.commit();
				if (shouldUpdate())
					updateContents();
			}
			return stack.copy().setAmount(extracted);
		}
	}

	@Override
	public FluidStack drain(long toExtract, boolean sim) {
		if (toExtract == 0)
			return FluidStack.EMPTY;
		if (!storage.supportsExtraction())
			return FluidStack.EMPTY;

		FluidStack extracted = FluidStack.EMPTY;
		try (Transaction t = Transaction.openOuter()) {
			for (StorageView<FluidVariant> view : storage.iterable(t)) {
				FluidVariant var = view.getResource();
				if (var.isBlank() || !extracted.canFill(var)) continue;
				long drained = view.extract(var, toExtract, t);
				toExtract -= drained;
				if (drained != 0) {
					if (extracted.isEmpty()) {
						extracted = new FluidStack(var, drained);
					} else if (extracted.canFill(var)) {
						extracted.grow(drained);
					}
				}
				if (toExtract <= 0) break;
			}
			if (!sim) {
				t.commit();
				if (shouldUpdate())
					updateContents();
			}
		}
		return extracted;
	}
}
