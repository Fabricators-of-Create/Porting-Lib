package io.github.fabricators_of_create.porting_lib.models.mixin;

import io.github.fabricators_of_create.porting_lib.models.extensions.BlockParticleOptionExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockParticleOption.class)
public class BlockParticleOptionMixin implements BlockParticleOptionExtensions {
	private BlockPos pos;

	@Override
	public BlockParticleOption setPos(BlockPos pos) {
		this.pos = pos;
		return (BlockParticleOption) (Object) this;
	}


	@Override
	public BlockPos getPos() {
		return pos;
	}
}
