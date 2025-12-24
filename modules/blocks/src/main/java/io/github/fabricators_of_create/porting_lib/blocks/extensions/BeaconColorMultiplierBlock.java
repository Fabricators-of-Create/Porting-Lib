package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

public interface BeaconColorMultiplierBlock {
	/**
	 * @param state     The state
	 * @param level     The level
	 * @param pos       The position of this state
	 * @param beaconPos The position of the beacon
	 * @return An Integer ARGB value to be averaged with a beacon's existing beam color, or null to do nothing to the beam
	 */
	@Nullable
	default Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
		if (this instanceof BeaconBeamBlock beamBlock)
			return beamBlock.getColor().getTextureDiffuseColor();
		return null;
	}
}
