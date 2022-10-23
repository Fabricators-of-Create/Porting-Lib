package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;

import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin extends ChunkAccess {
	@Shadow
	@Final
	private List<BlockPos> lights;

	@Shadow
	private volatile ChunkStatus status;

	@Shadow
	@Nullable
	private volatile LevelLightEngine lightEngine;

	public ProtoChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> registry, long l, @Nullable LevelChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
		super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
	}

	@Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I", ordinal = 0))
	public void port_lib$lightBlock(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> cir) {
		if(state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			if (lightEmissiveBlock.getLightEmission(state, this, pos) > 0) {
				this.lights.add(new BlockPos((i & 15) + this.getPos().getMinBlockX(), j, (k & 15) + this.getPos().getMinBlockZ()));
			}

			LevelChunkSection levelChunkSection = this.getSection(this.getSectionIndex(j));
			BlockState blockState = levelChunkSection.setBlockState(i & 15, j & 15, k & 15, state);
			if (this.status.isOrAfter(ChunkStatus.FEATURES) && state != blockState && (state.getLightBlock(this, pos) != blockState.getLightBlock(this, pos) || lightEmissiveBlock.getLightEmission(state, this, pos) != blockState.getLightEmission() || state.useShapeForLightOcclusion() || blockState.useShapeForLightOcclusion())) {
				this.lightEngine.checkBlock(pos);
			}
		}
	}
}
