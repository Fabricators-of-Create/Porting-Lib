package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
	@Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void port_lib$lightLevel(ServerLevel world, PoiManager poiStorage, ChunkPos pos, CompoundTag nbt, CallbackInfoReturnable<ProtoChunk> cir, boolean bl, ChunkStatus.ChunkType chunkType, ChunkAccess chunkAccess, ProtoChunk protoChunk, boolean bl6, Iterator var27, BlockPos blockPos) {
		BlockState state = chunkAccess.getBlockState(blockPos);
		if (state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock && lightEmissiveBlock.getLightEmission(state, chunkAccess, blockPos) != 0) {
			protoChunk.addLight(blockPos);
		}
	}
}
