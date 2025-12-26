package io.github.fabricators_of_create.porting_lib.models.injects;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

public interface BlockParticleOptionInjection {
	/**
	 * Give this BlockParticleOption the BlockPos of the block that created it.
	 */
	default BlockParticleOption port_lib$setSourcePos(BlockPos pos) {
		throw new AssertionError("Should be implemented in a mixin");
	}

	/**
	 * Get the BlockPos of the block that created this particle. May be null, not always available.
	 */
	@Nullable
	default BlockPos port_lib$getSourcePos() {
		throw new AssertionError("Should be implemented in a mixin");
	}
}
