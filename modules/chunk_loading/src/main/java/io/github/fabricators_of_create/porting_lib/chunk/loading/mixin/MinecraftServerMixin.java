package io.github.fabricators_of_create.porting_lib.chunk.loading.mixin;

import net.minecraft.server.level.progress.ChunkProgressListener;

import net.minecraft.world.level.TicketStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.chunk.loading.ForcedChunkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(method = "prepareLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/TicketStorage;activateAllDeactivatedTickets()V", shift = At.Shift.AFTER))
	private void reinstatePersistentChunks(ChunkProgressListener listener, CallbackInfo ci, @Local(ordinal = 1) ServerLevel level, @Local TicketStorage ticketStorage) {
		ForcedChunkManager.activateAllDeactivatedTickets(level, ticketStorage);
	}
}
