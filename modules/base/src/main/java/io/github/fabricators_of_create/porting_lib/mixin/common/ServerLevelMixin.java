package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
	protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
		super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
	}

	@Inject(method = "updateNeighborsAt", at = @At("HEAD"))
	private void neighborNotify(BlockPos pPos, Block block, CallbackInfo ci) {
		BlockEvents.NeighborNotifyEvent event = new BlockEvents.NeighborNotifyEvent(this, pPos, this.getBlockState(pPos), EnumSet.allOf(Direction.class), false);
		event.sendEvent();
	}

	@Inject(method = "updateNeighborsAtExceptFromFacing", at = @At("HEAD"), cancellable = true)
	private void neighborNotifyExpectFacing(BlockPos pPos, Block block, Direction pSkipSide, CallbackInfo ci) {
		EnumSet<Direction> directions =EnumSet.allOf(Direction.class);
		directions.remove(pSkipSide);
		BlockEvents.NeighborNotifyEvent event = new BlockEvents.NeighborNotifyEvent(this, pPos, this.getBlockState(pPos), directions, false);
		event.sendEvent();
		if (event.isCanceled())
			ci.cancel();
	}
}
