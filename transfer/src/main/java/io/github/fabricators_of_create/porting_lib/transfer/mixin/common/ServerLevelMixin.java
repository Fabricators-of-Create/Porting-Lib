package io.github.fabricators_of_create.porting_lib.transfer.mixin.common;

import io.github.fabricators_of_create.porting_lib.transfer.internal.extensions.LevelExtensions;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements LevelExtensions {
	@Override
	public BlockApiCache<Storage<ItemVariant>, Direction> port_lib$getItemCache(BlockPos pos) {
		return BlockApiCache.create(ItemStorage.SIDED, ((ServerLevel) (Object) this), pos);
	}

	@Override
	public BlockApiCache<Storage<FluidVariant>, Direction> port_lib$getFluidApiCache(BlockPos pos) {
		return BlockApiCache.create(FluidStorage.SIDED, ((ServerLevel) (Object) this), pos);
	}
}
