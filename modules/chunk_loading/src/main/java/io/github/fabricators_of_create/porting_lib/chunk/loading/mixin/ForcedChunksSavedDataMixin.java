package io.github.fabricators_of_create.porting_lib.chunk.loading.mixin;

import io.github.fabricators_of_create.porting_lib.chunk.loading.PortingLibChunkManager;
import io.github.fabricators_of_create.porting_lib.chunk.loading.extensions.ForcedChunksSavedDataExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ForcedChunksSavedData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForcedChunksSavedData.class)
public class ForcedChunksSavedDataMixin implements ForcedChunksSavedDataExtension {
	private PortingLibChunkManager.TicketTracker<net.minecraft.core.BlockPos> blockForcedChunks = new PortingLibChunkManager.TicketTracker<>();
	private PortingLibChunkManager.TicketTracker<java.util.UUID> entityForcedChunks = new PortingLibChunkManager.TicketTracker<>();

	@Inject(method = "load", at = @At("RETURN"))
	private static void readForgeForcedChunks(CompoundTag tag, CallbackInfoReturnable<ForcedChunksSavedData> cir) {
		ForcedChunksSavedData data = cir.getReturnValue();
		PortingLibChunkManager.readForgeForcedChunks(tag, data.getBlockForcedChunks(), data.getEntityForcedChunks());
	}

	@Inject(method = "save", at = @At("TAIL"))
	private void saveForgeForcedChunks(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
		PortingLibChunkManager.writeForgeForcedChunks(compoundTag, getBlockForcedChunks(), getEntityForcedChunks());
	}

	@Override
	public PortingLibChunkManager.TicketTracker<net.minecraft.core.BlockPos> getBlockForcedChunks() {
		return this.blockForcedChunks;
	}

	@Override
	public PortingLibChunkManager.TicketTracker<java.util.UUID> getEntityForcedChunks() {
		return this.entityForcedChunks;
	}
}
