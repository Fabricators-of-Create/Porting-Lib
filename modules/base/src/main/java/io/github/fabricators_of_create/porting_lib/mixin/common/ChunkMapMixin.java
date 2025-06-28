package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.ChunkWatchEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {
	@Inject(method = "markChunkPendingToSend(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At("TAIL"))
	private static void watchChunk(ServerPlayer player, LevelChunk chunk, CallbackInfo ci) {
		new ChunkWatchEvent.Watch(player, chunk, player.serverLevel()).sendEvent();
	}

	@Inject(method = "dropChunk", at = @At("HEAD"))
	private static void unwatchChunk(ServerPlayer player, ChunkPos chunkPos, CallbackInfo ci) {
		new ChunkWatchEvent.UnWatch(player, chunkPos, player.serverLevel()).sendEvent();
	}
}
