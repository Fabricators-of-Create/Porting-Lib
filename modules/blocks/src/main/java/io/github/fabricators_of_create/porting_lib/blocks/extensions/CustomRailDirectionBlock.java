package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

import org.jetbrains.annotations.Nullable;

/**
 * Implement on classes extending {@link net.minecraft.world.level.block.BaseRailBlock}
 */
public interface CustomRailDirectionBlock {
	/**
	 * Return the rail's direction.
	 * Can be used to make the cart think the rail is a different shape,
	 * for example when making diamond junctions or switches.
	 * The cart parameter will often be null unless it is called from {@link AbstractMinecart}.
	 *
	 * @param level The level.
	 * @param pos Block's position in level
	 * @param state The BlockState
	 * @param cart The cart asking for the metadata, null if it is not called by {@link AbstractMinecart}.
	 * @return The direction.
	 */
	default RailShape getRailDirection(BlockState state, BlockGetter level, BlockPos pos, @Nullable AbstractMinecart cart) {
		return state.getValue(((BaseRailBlock) this).getShapeProperty());
	}
}
