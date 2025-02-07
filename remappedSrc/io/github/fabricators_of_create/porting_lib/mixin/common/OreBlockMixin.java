package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OreBlock.class)
public abstract class OreBlockMixin implements CustomExpBlock {
	@Shadow
	@Final
	private UniformIntProvider xpRange;

	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.WorldView reader, BlockPos pos, int fortune, int silktouch) {
		return silktouch == 0 ? this.xpRange.get(((World)reader).getRandom()) : 0;
	}
}
