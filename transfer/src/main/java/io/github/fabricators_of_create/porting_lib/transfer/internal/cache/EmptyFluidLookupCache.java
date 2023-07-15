package io.github.fabricators_of_create.porting_lib.transfer.internal.cache;

import io.github.fabricators_of_create.porting_lib.util.StorageProvider;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This should not be used directly. Use {@link StorageProvider} instead.
 */
@Internal
@SuppressWarnings("NonExtendableApiUsage")
public record EmptyFluidLookupCache(BlockPos pos) implements BlockApiCache<Storage<FluidVariant>, Direction> {
	@Override
	@Nullable
	public Storage<FluidVariant> find(@Nullable BlockState state, Direction context) {
		return null;
	}

	@Override
	@Nullable
	public BlockEntity getBlockEntity() {
		return null;
	}

	@Override
	public BlockApiLookup<Storage<FluidVariant>, Direction> getLookup() {
		return FluidStorage.SIDED;
	}

	@Override
	public ServerLevel getWorld() {
		throw new UnsupportedOperationException("Cannot call getWorld on an empty cache as no world is associated with it");
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}
}
