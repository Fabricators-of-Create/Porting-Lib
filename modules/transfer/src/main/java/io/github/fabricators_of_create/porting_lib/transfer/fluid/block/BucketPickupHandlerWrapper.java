package io.github.fabricators_of_create.porting_lib.transfer.fluid.block;

import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionSuccessCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.material.FluidState;

public class BucketPickupHandlerWrapper implements SingleSlotStorage<FluidVariant>, ExtractionOnlyStorage<FluidVariant> {
	protected final BucketPickup bucketPickupHandler;
	protected final Level world;
	protected final BlockPos blockPos;

	public BucketPickupHandlerWrapper(BucketPickup bucketPickupHandler, Level world, BlockPos blockPos) {
		this.bucketPickupHandler = bucketPickupHandler;
		this.world = world;
		this.blockPos = blockPos;
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext tx) {
		if (!resource.isBlank() && FluidConstants.BUCKET <= maxAmount) {
			FluidState fluidState = world.getFluidState(blockPos);
			if (!fluidState.isEmpty() && resource.getFluid() == fluidState.getType()) {
				TransactionSuccessCallback.onSuccess(tx, () -> bucketPickupHandler.pickupBlock(null, world, blockPos, world.getBlockState(blockPos)));
				if (resource.equals(FluidVariant.of(fluidState.getType()))) {
					return FluidConstants.BUCKET;
				}
			}
		}
		return 0;
	}

	@Override
	public boolean isResourceBlank() {
		return getResource().isBlank();
	}

	@Override
	public FluidVariant getResource() {
		return FluidVariant.of(world.getFluidState(blockPos).getType());
	}

	@Override
	public long getAmount() {
		return !world.getFluidState(blockPos).isEmpty() ? FluidConstants.BUCKET : 0;
	}

	@Override
	public long getCapacity() {
		return FluidConstants.BUCKET;
	}
}
