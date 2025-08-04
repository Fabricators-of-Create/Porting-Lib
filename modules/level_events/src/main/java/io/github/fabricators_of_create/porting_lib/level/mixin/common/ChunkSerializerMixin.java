package io.github.fabricators_of_create.porting_lib.level.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.level.events.ChunkDataEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;

import net.minecraft.world.level.chunk.storage.RegionStorageInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
	@Inject(method = "read", at = @At(value = "RETURN", ordinal = 0))
	private static void onChunkDataLoad0(ServerLevel level, PoiManager poiManager, RegionStorageInfo regionStorageInfo, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir, @Local ChunkAccess chunkAccess, @Local ChunkType chunktype) {
		new ChunkDataEvent.Load(chunkAccess, tag, chunktype).sendEvent();
	}

	@Inject(method = "read", at = @At(value = "RETURN", ordinal = 1))
	private static void onChunkDataLoad1(ServerLevel level, PoiManager poiManager, RegionStorageInfo regionStorageInfo, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir, @Local ChunkAccess chunkAccess, @Local ChunkType chunktype) {
		new ChunkDataEvent.Load(chunkAccess, tag, chunktype).sendEvent();
	}
}
