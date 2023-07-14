package io.github.fabricators_of_create.porting_lib.models.mixin;

import io.github.fabricators_of_create.porting_lib.models.extensions.BlockParticleOptionExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockParticleOption.class)
public class BlockParticleOptionMixin implements BlockParticleOptionExtensions {
	@Unique
	@Nullable
	private BlockPos sourcePos;

	@Override
	public BlockParticleOption setSourcePos(BlockPos pos) {
		this.sourcePos = pos;
		return (BlockParticleOption) (Object) this;
	}


	@Override
	public BlockPos getSourcePos() {
		return sourcePos;
	}
}
