package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import io.github.fabricators_of_create.porting_lib.block.ChunkUnloadListeningBlockEntity;
import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.LevelExtensions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.CustomUpdateTagHandlingBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.level.levelgen.blending.BlendingData;

import java.util.stream.Stream;


@Mixin(WorldChunk.class)
public abstract class LevelChunkMixin extends Chunk {
	@Shadow
	@Final
	World level;

	@Shadow
	public abstract BlockState getBlockState(BlockPos pos);

	@Shadow
	public abstract World getLevel();

	public LevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, HeightLimitView levelHeightAccessor, Registry<Biome> registry, long l, @Nullable ChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
		super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
	}

	@Inject(method = "clearAllBlockEntities", at = @At("HEAD"))
	private void port_lib$blockEntityClear(CallbackInfo ci) {
		blockEntities.values().forEach(be -> {
			if (be instanceof ChunkUnloadListeningBlockEntity listener) {
				listener.onChunkUnloaded();
			}
		});
	}

	@Inject(method = "method_31716",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;load(Lnet/minecraft/nbt/CompoundTag;)V"),
			locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true
	)
	private void port_lib$handleBlockEntityUpdateTag(BlockPos pos, BlockEntityType<?> type, NbtCompound tag, CallbackInfo ci, BlockEntity blockEntity) {
		if (blockEntity instanceof CustomUpdateTagHandlingBlockEntity handler) {
			handler.handleUpdateTag(tag);
			ci.cancel();
		}
	}

	@Inject(method = "addAndRegisterBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;updateBlockEntityTicker(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", shift = At.Shift.AFTER))
	public void port_lib$onBlockEntityLoad(BlockEntity blockEntity, CallbackInfo ci) {
		blockEntity.onLoad();
	}

	@Inject(method = "registerAllBlockEntitiesAfterLevelLoad", at = @At("HEAD"))
	public void port_lib$addPendingBlockEntities(CallbackInfo ci) {
		this.level.addFreshBlockEntities(this.blockEntities.values());
	}

	@Inject(method = "method_12217", at = @At("HEAD"), cancellable = true)
	public void port_lib$lightBlock(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
		BlockState state = getBlockState(blockPos);
		if (state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock) {
			cir.setReturnValue(lightEmissiveBlock.getLightEmission(state, getLevel(), blockPos) != 0);
		}
	}
}
