package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.mojang.serialization.Codec;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
	@Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void port_lib$lightLevel(ServerWorld lvel, PointOfInterestStorage poiManager, ChunkPos pos, NbtCompound tag, CallbackInfoReturnable<ProtoChunk> cir, ChunkPos chunkPos, UpgradeData data, boolean flag, NbtList listTag, int i, ChunkSection[] section, boolean flag2, ChunkManager source, LightingProvider engine, Registry<?> registry, Codec codec, long l, ChunkStatus.ChunkType type, BlendingData blendingData, Chunk chunkAccess, ProtoChunk protoChunk, boolean flag3, Iterator var26, BlockPos blockPos) {
		BlockState state = chunkAccess.getBlockState(blockPos);
		if (state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock && lightEmissiveBlock.getLightEmission(state, chunkAccess, blockPos) != 0) {
			protoChunk.addLightSource(blockPos);
		}
	}
}
