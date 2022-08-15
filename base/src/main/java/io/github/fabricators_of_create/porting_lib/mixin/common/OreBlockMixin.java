package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(OreBlock.class)
public abstract class OreBlockMixin implements CustomExpBlock {
	@Shadow
	@Final
	private UniformInt xpRange;

	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader reader, BlockPos pos, int fortune, int silktouch) {
		return silktouch == 0 ? this.xpRange.sample(((Level)reader).getRandom()) : 0;
	}
}
