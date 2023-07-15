package io.github.fabricators_of_create.porting_lib.transfer.internal.extensions;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

@Internal
public interface LevelExtensions {
	default BlockApiCache<Storage<ItemVariant>, Direction> port_lib$getItemCache(BlockPos pos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default BlockApiCache<Storage<FluidVariant>, Direction> port_lib$getFluidApiCache(BlockPos pos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
