package io.github.fabricators_of_create.porting_lib.level.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.level.LevelHooks;
import io.github.fabricators_of_create.porting_lib.level.events.ChunkDataEvent;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import net.minecraft.world.level.chunk.storage.SerializableChunkData;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
	@Shadow
	@Final
	ServerLevel level;

	@Inject(method = "updateChunkScheduling", at = @At(value = "RETURN", ordinal = 1))
	private void callChunkTicketLevelUpdated(long chunkPos, int newLevel, ChunkHolder holder, int oldLevel, CallbackInfoReturnable<ChunkHolder> cir) {
		LevelHooks.fireChunkTicketLevelUpdated(level, chunkPos, oldLevel, newLevel, cir.getReturnValue());
	}

	@Inject(method = "method_43375", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;markPosition(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/status/ChunkType;)B"))
	private void onChunkDataLoad(ChunkPos chunkPos, Optional<SerializableChunkData> optional, CallbackInfoReturnable<ChunkAccess> cir, @Local ChunkAccess chunkAccess) {
		new ChunkDataEvent.Load(chunkAccess, optional.get()).sendEvent();
	}

	@Inject(method = "save", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
	private void onChunkDataSave(ChunkAccess chunk, CallbackInfoReturnable<Boolean> cir, @Local SerializableChunkData serializableChunkData) {
		new ChunkDataEvent.Save(chunk, this.level, serializableChunkData).sendEvent();
	}
}
