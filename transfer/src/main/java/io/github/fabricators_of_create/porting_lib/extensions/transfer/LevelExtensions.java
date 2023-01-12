package io.github.fabricators_of_create.porting_lib.extensions.transfer;

import io.github.fabricators_of_create.porting_lib.transfer.cache.EmptyFluidLookupCache;
import io.github.fabricators_of_create.porting_lib.transfer.cache.EmptyItemLookupCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface LevelExtensions {
	default BlockApiCache<Storage<ItemVariant>, Direction> port_lib$getItemCache(BlockPos pos) {
		// litematica compat: don't throw, just return empty
		return EmptyItemLookupCache.INSTANCE;
	}

	default BlockApiCache<Storage<FluidVariant>, Direction> port_lib$getFluidApiCache(BlockPos pos) {
		return EmptyFluidLookupCache.INSTANCE;
	}
}
