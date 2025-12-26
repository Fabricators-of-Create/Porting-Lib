package io.github.fabricators_of_create.porting_lib.models.mixin;

import io.github.fabricators_of_create.porting_lib.models.injects.BlockParticleOptionInjection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockParticleOption.class)
public class BlockParticleOptionMixin implements BlockParticleOptionInjection {
	@Unique
	@Nullable
	private BlockPos sourcePos;

	@Override
	public BlockParticleOption port_lib$setSourcePos(BlockPos pos) {
		this.sourcePos = pos;
		return (BlockParticleOption) (Object) this;
	}


	@Override
	public BlockPos port_lib$getSourcePos() {
		return sourcePos;
	}
}
