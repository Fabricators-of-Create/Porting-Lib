package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockStateExtensions {
	/**
	 * Determines if this block can support the passed in plant, allowing it to be planted and grow.
	 * Some examples:
	 *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
	 *   Cacti checks if its a cacti, or if its sand
	 *   Nether types check for soul sand
	 *   Crops check for tilled soil
	 *   Caves check if it's a solid surface
	 *   Plains check if its grass or dirt
	 *   Water check if its still water
	 *
	 * @param level The current level
	 * @param facing The direction relative to the given position the plant wants to be, typically its UP
	 * @param plantable The plant that wants to check
	 * @return True to allow the plant to be planted/stay.
	 */
	default boolean canSustainPlant(BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
		return ((BlockState)this).getBlock().canSustainPlant(((BlockState)this), level, pos, facing, plantable);
	}
}
