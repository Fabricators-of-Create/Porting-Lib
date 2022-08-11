package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import io.github.fabricators_of_create.porting_lib.block.ChunkUnloadListeningBlockEntity;
import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.LevelExtensions;

import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockState;

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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;

import java.util.stream.Stream;


@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess {
	@Shadow
	@Final
	Level level;

	@Shadow
	public abstract BlockState getBlockState(BlockPos pos);

	@Shadow
	public abstract Level getLevel();

	public LevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> registry, long l, @Nullable LevelChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
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

	@Inject(method = {"method_31716", "lambda$replaceWithPacketData$3", "m_pptwysxt"},
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;load(Lnet/minecraft/nbt/CompoundTag;)V"),
			locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true
	)
	private void port_lib$handleBlockEntityUpdateTag(BlockPos pos, BlockEntityType<?> type, CompoundTag tag, CallbackInfo ci, BlockEntity blockEntity) {
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

	@Inject(method = {"method_12217", "lambda$getLights$4", "m_eaodltdq"}, at = @At("HEAD"), cancellable = true)
	public void port_lib$lightBlock(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
		BlockState state = getBlockState(blockPos);
		if (state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock) {
			cir.setReturnValue(lightEmissiveBlock.getLightEmission(state, getLevel(), blockPos) != 0);
		}
	}
}
