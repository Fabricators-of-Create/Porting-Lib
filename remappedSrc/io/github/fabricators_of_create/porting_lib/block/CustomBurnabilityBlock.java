package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;

/**
 * Allows blocks to control their burnability based on their state
 * @see FireBlock#isFlammable(BlockState)
 */
public interface CustomBurnabilityBlock {
	/**
	 * @return true if the provided state is burnable by fire
	 */
	boolean canBurn(BlockState state);
}
