package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin extends Chunk {
	@Shadow
	@Final
	private List<BlockPos> lights;

	@Shadow
	private volatile ChunkStatus status;

	@Shadow
	@Nullable
	private volatile LightingProvider lightEngine;

	public ProtoChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, HeightLimitView levelHeightAccessor, Registry<Biome> registry, long l, @Nullable ChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
		super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
	}

	@Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I", ordinal = 0))
	public void port_lib$lightBlock(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> cir) {
		if(state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			if (lightEmissiveBlock.getLightEmission(state, this, pos) > 0) {
				this.lights.add(new BlockPos((i & 15) + this.getPos().getStartX(), j, (k & 15) + this.getPos().getStartZ()));
			}

			ChunkSection levelChunkSection = this.getSection(this.getSectionIndex(j));
			BlockState blockState = levelChunkSection.setBlockState(i & 15, j & 15, k & 15, state);
			if (this.status.isAtLeast(ChunkStatus.FEATURES) && state != blockState && (state.getOpacity(this, pos) != blockState.getOpacity(this, pos) || lightEmissiveBlock.getLightEmission(state, this, pos) != blockState.getLuminance() || state.hasSidedTransparency() || blockState.hasSidedTransparency())) {
				this.lightEngine.checkBlock(pos);
			}
		}
	}
}
