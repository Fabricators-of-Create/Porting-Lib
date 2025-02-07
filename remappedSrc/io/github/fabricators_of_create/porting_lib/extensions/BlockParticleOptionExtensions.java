package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.entity.Entity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface BlockParticleOptionExtensions {
	/**
	 * Give this BlockParticleOption the BlockPos of the block that created it.
	 */
	default BlockStateParticleEffect setSourcePos(BlockPos pos) {
		throw new AssertionError("Should be implemented in a mixin");
	}

	/**
	 * Get the BlockPos of the block that created this particle. May be null, not always available.
	 */
	@Nullable
	default BlockPos getSourcePos() {
		throw new AssertionError("Should be implemented in a mixin");
	}

	/**
	 * Helper to set the source to the block an entity is standing on.
	 */
	static ParticleEffect setSourceFromEntity(ParticleEffect options, Entity entity) {
		if (options instanceof BlockStateParticleEffect block) {
			BlockPos posBelow = new BlockPos(entity.getPos().subtract(0, 0.2, 0));
			block.setSourcePos(posBelow);
		}
		return options;
	}
}
