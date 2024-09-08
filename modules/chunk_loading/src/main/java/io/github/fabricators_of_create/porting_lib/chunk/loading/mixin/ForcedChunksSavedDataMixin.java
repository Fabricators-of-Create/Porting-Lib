package io.github.fabricators_of_create.porting_lib.chunk.loading.mixin;

import io.github.fabricators_of_create.porting_lib.chunk.loading.ForcedChunkManager;
import io.github.fabricators_of_create.porting_lib.chunk.loading.extensions.ForcedChunksSavedDataExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ForcedChunksSavedData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ForcedChunksSavedData.class)
public class ForcedChunksSavedDataMixin implements ForcedChunksSavedDataExtension {
	// Neo: Keep track of forced loaded chunks caused by entities or blocks.
	private final ForcedChunkManager.TicketTracker<BlockPos> port_lib$blockForcedChunks = new ForcedChunkManager.TicketTracker<>();
	private final ForcedChunkManager.TicketTracker<UUID> port_lib$entityForcedChunks = new ForcedChunkManager.TicketTracker<>();

	@Inject(method = "load", at = @At("RETURN"))
	private static void readForgeForcedChunks(CompoundTag tag, HolderLookup.Provider provider, CallbackInfoReturnable<ForcedChunksSavedData> cir) {
		ForcedChunksSavedData data = cir.getReturnValue();
		ForcedChunkManager.readModForcedChunks(tag, data.getBlockForcedChunks(), data.getEntityForcedChunks());
	}

	@Inject(method = "save", at = @At("TAIL"))
	private void saveForgeForcedChunks(CompoundTag tag, HolderLookup.Provider provider, CallbackInfoReturnable<CompoundTag> cir) {
		ForcedChunkManager.writeModForcedChunks(tag, getBlockForcedChunks(), getEntityForcedChunks());
	}

	@Override
	public ForcedChunkManager.TicketTracker<BlockPos> getBlockForcedChunks() {
		return this.port_lib$blockForcedChunks;
	}

	@Override
	public ForcedChunkManager.TicketTracker<UUID> getEntityForcedChunks() {
		return this.port_lib$entityForcedChunks;
	}
}
