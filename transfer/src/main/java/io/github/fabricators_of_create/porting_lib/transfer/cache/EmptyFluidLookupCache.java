package io.github.fabricators_of_create.porting_lib.transfer.cache;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("NonExtendableApiUsage")
public enum EmptyFluidLookupCache implements BlockApiCache<Storage<FluidVariant>, Direction> {
	INSTANCE;

	@Override
	public @Nullable Storage<FluidVariant> find(@Nullable BlockState state, Direction context) {
		return null;
	}

	@Override
	public @Nullable BlockEntity getBlockEntity() {
		return null;
	}

	@Override
	public BlockApiLookup<Storage<FluidVariant>, Direction> getLookup() {
		return FluidStorage.SIDED;
	}

	@Override
	public ServerLevel getWorld() {
		return null;
	}

	@Override
	public BlockPos getPos() {
		return null;
	}
}
