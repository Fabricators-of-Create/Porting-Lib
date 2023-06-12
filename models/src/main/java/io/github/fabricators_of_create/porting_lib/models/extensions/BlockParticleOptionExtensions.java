package io.github.fabricators_of_create.porting_lib.models.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

public interface BlockParticleOptionExtensions {
	BlockParticleOption setPos(BlockPos pos);

	BlockPos getPos();
}
