package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

// Porting Lib: Neo never fixed these methods not being called
public interface MinecartPassHandlerBlock {
	/**
	 * This function is called by any minecart that passes over this rail.
	 * It is called once per update tick that the minecart is on the rail.
	 *
	 * @param level The level.
	 * @param cart  The cart on the rail.
	 * @param pos   Block's position in level
	 */
	default void onMinecartPass(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {}
}
