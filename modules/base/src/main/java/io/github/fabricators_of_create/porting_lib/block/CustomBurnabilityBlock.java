package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Allows blocks to control their burnability based on their state
 * @see FireBlock#canBurn(BlockState)
 */
public interface CustomBurnabilityBlock {
	/**
	 * @return true if the provided state is burnable by fire
	 */
	boolean canBurn(BlockState state);
}
