package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.mojang.serialization.Codec;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;

import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
	@Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void port_lib$lightLevel(ServerLevel lvel, PoiManager poiManager, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir, ChunkPos chunkPos, UpgradeData data, boolean flag, ListTag listTag, int i, LevelChunkSection[] section, boolean flag2, ChunkSource source, LevelLightEngine engine, Registry<?> registry, Codec codec, long l, ChunkStatus.ChunkType type, BlendingData blendingData, ChunkAccess chunkAccess, ProtoChunk protoChunk, boolean flag3, Iterator var26, BlockPos blockPos) {
		BlockState state = chunkAccess.getBlockState(blockPos);
		if (state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock && lightEmissiveBlock.getLightEmission(state, chunkAccess, blockPos) != 0) {
			protoChunk.addLight(blockPos);
		}
	}
}
