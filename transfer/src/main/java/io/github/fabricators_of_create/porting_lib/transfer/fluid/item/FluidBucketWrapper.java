package io.github.fabricators_of_create.porting_lib.transfer.fluid.item;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.BucketItemAccessor;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import xyz.bluspring.forgecapabilities.capabilities.Capability;
import xyz.bluspring.forgecapabilities.capabilities.ForgeCapabilities;
import xyz.bluspring.forgecapabilities.capabilities.ICapabilityProvider;

@ApiStatus.Experimental
public class FluidBucketWrapper implements SingleSlotStorage<FluidVariant>, ICapabilityProvider {
	private final LazyOptional<SingleSlotStorage<FluidVariant>> holder = LazyOptional.of(() -> this);

	@NotNull
	protected ContainerItemContext context;

	public FluidBucketWrapper(@NotNull ContainerItemContext context) {
		this.context = context;
	}

	public boolean canFillFluidType(FluidVariant resource, long amount) {
		if (resource.getFluid() == Fluids.WATER || resource.getFluid() == Fluids.LAVA) {
			return true;
		}
		return !new ItemStack(resource.getFluid().getBucket()).isEmpty();
	}

	@NotNull
	public FluidStack getFluid() {
		Item item = context.getItemVariant().getItem();
		if (item instanceof BucketItem) {
			return new FluidStack(((BucketItemAccessor)item).port_lib$getContent(), FluidConstants.BUCKET);
		} else {
			return FluidStack.EMPTY;
		}
	}

	protected void setFluid(@NotNull FluidStack fluidStack) {
		try (Transaction tx = TransferUtil.getTransaction()) {
			if (fluidStack.isEmpty()) {
				if (context.exchange(ItemVariant.of(Items.BUCKET), 1, tx) == 1)
					tx.commit();
			} else{
				if (context.exchange(ItemVariant.of(getFilledBucket(fluidStack)), 1, tx) == 1)
					tx.commit();
			}
		}
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
		return FluidConstants.BUCKET;
	}

	@Override
	public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		if (context.getAmount() != 1 || maxAmount < FluidConstants.BUCKET || context.getItemVariant().getItem() instanceof MilkBucketItem || !new FluidStack(resource, maxAmount).isEmpty() || !canFillFluidType(resource, maxAmount)) {
			return 0;
		}

		setFluid(new FluidStack(resource, maxAmount));

		return FluidConstants.BUCKET;
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		if (context.getAmount() != 1 || maxAmount < FluidConstants.BUCKET) {
			return 0;
		}

		FluidStack fluidStack = getFluid();
		if (!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource)) {
			setFluid(FluidStack.EMPTY);
			return fluidStack.getAmount();
		}

		return 0;
	}

	@NotNull
	public ItemStack getFilledBucket(@NotNull FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();

		if (!fluidStack.hasTag() || fluidStack.getTag().isEmpty()) {
			if (fluid == Fluids.WATER) {
				return new ItemStack(Items.WATER_BUCKET);
			} else if (fluid == Fluids.LAVA) {
				return new ItemStack(Items.LAVA_BUCKET);
			}
		}

		return new ItemStack(fluidStack.getFluid().getBucket());
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(cap, this.holder);
	}
}
