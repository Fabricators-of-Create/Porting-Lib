package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.BlockParticleOptionExtensions;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockStateParticleEffect.class)
public class BlockParticleOptionMixin implements BlockParticleOptionExtensions {
	@Unique
	@Nullable
	private BlockPos sourcePos;

	@Override
	public BlockStateParticleEffect setSourcePos(BlockPos pos) {
		this.sourcePos = pos;
		return (BlockStateParticleEffect) (Object) this;
	}

	@Override
	@Nullable
	public BlockPos getSourcePos() {
		return sourcePos;
	}
}
