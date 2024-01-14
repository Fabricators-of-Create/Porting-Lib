package io.github.fabricators_of_create.porting_lib.models.extensions;

import io.github.fabricators_of_create.porting_lib.models.CustomParticleIconModel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface TerrainParticleExtensions {
	/**
	 * Refresh this particle's sprite using {@link BakedModel#getParticleIcon()} or {@link CustomParticleIconModel#getParticleIcon(Object)}.
	 * The model is gotten from the block state.
	 */
	default TerrainParticle updateSprite(BlockState state, BlockPos pos) {
		throw new AssertionError("Should be implemented in a mixin");
	}
}
