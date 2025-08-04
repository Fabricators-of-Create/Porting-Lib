package io.github.fabricators_of_create.porting_lib.level.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.level.events.ChunkDataEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

import net.minecraft.world.level.chunk.LevelChunk;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {
	@Shadow
	@Final
	private ServerLevel level;

	@Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;write(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Ljava/util/concurrent/CompletableFuture;"))
	private void onChunkDataSave(ChunkAccess chunk, CallbackInfoReturnable<Boolean> cir, @Local CompoundTag tag) {
		Level level;
		if (chunk instanceof LevelChunk levelChunk)
			level = levelChunk.getLevel() != null ? levelChunk.getLevel() : this.level;
		else
			level = this.level;
		new ChunkDataEvent.Save(chunk, level, tag).sendEvent();
	}
}
