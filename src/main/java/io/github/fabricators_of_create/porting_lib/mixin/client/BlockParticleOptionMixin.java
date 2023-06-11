package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.BlockParticleOptionExtensions;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

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
	@Nullable
	public BlockPos getSourcePos() {
		return sourcePos;
	}
}
