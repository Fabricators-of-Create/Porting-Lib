package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.model.CustomParticleIconModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;

public interface TerrainParticleExtensions {
	/**
	 * Refresh this particle's sprite using {@link BakedModel#getParticleSprite()} or {@link CustomParticleIconModel#getParticleIcon(Object)}.
	 * The model is gotten from the block state.
	 */
	default BlockDustParticle updateSprite(BlockState state, BlockPos pos) {
		throw new AssertionError("Should be implemented in a mixin");
	}
}
