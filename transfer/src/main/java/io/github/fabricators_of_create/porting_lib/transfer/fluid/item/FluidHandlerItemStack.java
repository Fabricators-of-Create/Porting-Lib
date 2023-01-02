package io.github.fabricators_of_create.porting_lib.transfer.fluid.item;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@ApiStatus.Experimental
public class FluidHandlerItemStack implements Storage<FluidVariant> {
	public static final String FLUID_NBT_KEY = "Fluid";

	@NotNull
	protected ContainerItemContext container;
	protected long capacity;

	/**
	 * @param container  The container {@link ContainerItemContext}, data is stored on the {@link ItemVariant} NBT.
	 * @param capacity   The maximum capacity of this fluid tank.
	 */
	public FluidHandlerItemStack(@NotNull ContainerItemContext container, long capacity) {
		this.container = container;
		this.capacity = capacity;
	}

	@NotNull
	public FluidStack getFluid() {
		CompoundTag tagCompound = container.getItemVariant().getNbt();
		if (tagCompound == null || !tagCompound.contains(FLUID_NBT_KEY)) {
			return FluidStack.EMPTY;
		}
		return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
	}

	protected boolean setFluid(FluidStack fluid, TransactionContext tx) {
		ItemStack newStack = container.getItemVariant().toStack();
		if (!newStack.hasTag()) {
			newStack.setTag(new CompoundTag());
		}

		CompoundTag fluidTag = new CompoundTag();
		fluid.writeToNBT(fluidTag);
		newStack.getTag().put(FLUID_NBT_KEY, fluidTag);

		if (container.exchange(ItemVariant.of(newStack), 1, tx) == 1)
			return true;
		return false;
	}


	@Override
	public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		if (container.getAmount() != 1 || new FluidStack(resource, maxAmount).isEmpty() || !canFillFluidType(resource, maxAmount))
			return 0;
		FluidStack contained = getFluid();
		if (contained.isEmpty()) {
			long fillAmount = Math.min(capacity, maxAmount);

			FluidStack filled = new FluidStack(resource, maxAmount);
			filled.setAmount(fillAmount);
			if (setFluid(filled, transaction))
				return fillAmount;
		} else {
			if (contained.isFluidEqual(resource)) {
				long fillAmount = Math.min(capacity - contained.getAmount(), maxAmount);

				if (fillAmount > 0) {
					contained.grow(fillAmount);
					if (setFluid(contained, transaction))
						return fillAmount;
				}
			}
		}

		return 0;
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		if (container.getAmount() != 1 || new FluidStack(resource, maxAmount).isEmpty() || !FluidStack.isFluidEqual(resource, getFluid().getType()) || maxAmount <= 0)
			return 0;
		FluidStack contained = getFluid();
		if (contained.isEmpty() || !canDrainFluidType(contained.getType(), contained.getAmount()))
		{
			return 0;
		}

		final long drainAmount = Math.min(contained.getAmount(), maxAmount);

		contained.shrink(drainAmount);
		if (contained.isEmpty()) {
			if (setContainerToEmpty(transaction))
				return drainAmount;
		} else {
			if (setFluid(contained, transaction))
				return drainAmount;
		}

		return 0;
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator() {
		return new Iterator<>() {
			boolean hasNext = true;

			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public StorageView<FluidVariant> next() {
				if (!hasNext) {
					throw new NoSuchElementException();
				}

				hasNext = false;
				return new FluidHandlerItemStackView();
			}
		};
	}

	public class FluidHandlerItemStackView implements StorageView<FluidVariant> {

		@Override
		public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
			return FluidHandlerItemStack.this.extract(resource, maxAmount, transaction);
		}

		@Override
		public boolean isResourceBlank() {
			return getResource().isBlank();
		}

		@Override
		public FluidVariant getResource() {
			return getFluid().getType();
		}

		@Override
		public long getAmount() {
			return getFluid().getAmount();
		}

		@Override
		public long getCapacity() {
			return capacity;
		}
	}

	public boolean canFillFluidType(FluidVariant variant, long amount) {
		return true;
	}

	public boolean canDrainFluidType(FluidVariant variant, long amount) {
		return true;
	}

	/**
	 * Override this method for special handling.
	 * Can be used to swap out or destroy the container.
	 * @param tx The current transaction to use.
	 * @return returns true if the container was successfully emptied.
	 */
	protected boolean setContainerToEmpty(TransactionContext tx) {
		ItemStack newStack = container.getItemVariant().toStack();
		newStack.removeTagKey(FLUID_NBT_KEY);
		if (container.exchange(ItemVariant.of(newStack), 1, tx) == 1)
			return true;
		return false;
	}

	/**
	 * Destroys the container item when it's emptied.
	 */
	public static class Consumable extends FluidHandlerItemStack {
		public Consumable(ContainerItemContext container, int capacity) {
			super(container, capacity);
		}

		@Override
		protected boolean setContainerToEmpty(TransactionContext tx) {
			boolean result = super.setContainerToEmpty(tx);
			try (Transaction nested = tx.openNested()) {
				if (container.extract(container.getItemVariant(), 1, nested) == 1) {
					nested.commit();
					return true;
				}
			}
			return result;
		}
	}

	/**
	 * Swaps the container item for a different one when it's emptied.
	 */
	public static class SwapEmpty extends FluidHandlerItemStack {
		protected final ItemStack emptyContainer;

		public SwapEmpty(ContainerItemContext container, ItemStack emptyContainer, int capacity) {
			super(container, capacity);
			this.emptyContainer = emptyContainer;
		}

		@Override
		protected boolean setContainerToEmpty(TransactionContext tx) {
			boolean result = super.setContainerToEmpty(tx);
			try (Transaction nested = tx.openNested()) {
				if (container.exchange(ItemVariant.of(emptyContainer), emptyContainer.getCount(), nested) == emptyContainer.getCount()) {
					nested.commit();
					return true;
				}
			}
			return result;
		}
	}
}
