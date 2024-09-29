package io.github.fabricators_of_create.porting_lib.models.extensions;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;

public interface BlockParticleOptionExtensions {
	/**
	 * Give this BlockParticleOption the BlockPos of the block that created it.
	 */
	default BlockParticleOption setSourcePos(BlockPos pos) {
		throw new AssertionError("Should be implemented in a mixin");
	}

	/**
	 * Get the BlockPos of the block that created this particle. May be null, not always available.
	 */
	@Nullable
	default BlockPos getSourcePos() {
		throw new AssertionError("Should be implemented in a mixin");
	}
}
