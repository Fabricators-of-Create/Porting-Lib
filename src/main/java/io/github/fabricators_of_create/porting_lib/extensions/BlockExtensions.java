package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.PlantType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockExtensions {
	default boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		BlockState plant = plantable.getPlant(world, pos.relative(facing));
		PlantType type = plantable.getPlantType(world, pos.relative(facing));

		if (plant.getBlock() == Blocks.CACTUS)
			return state.is(Blocks.CACTUS) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);

		if (plant.getBlock() == Blocks.SUGAR_CANE && this == Blocks.SUGAR_CANE)
			return true;

		if (plantable instanceof BushBlock && ((BushBlock)plantable).mayPlaceOn(state, world, pos))
			return true;

		if (PlantType.DESERT.equals(type)) {
			return this == Blocks.SAND || this == Blocks.TERRACOTTA || this instanceof GlazedTerracottaBlock;
		} else if (PlantType.NETHER.equals(type)) {
			return this == Blocks.SOUL_SAND;
		} else if (PlantType.CROP.equals(type)) {
			return state.is(Blocks.FARMLAND);
		} else if (PlantType.CAVE.equals(type)) {
			return state.isFaceSturdy(world, pos, Direction.UP);
		} else if (PlantType.PLAINS.equals(type)) {
			return this == Blocks.GRASS_BLOCK || ((Block)this).defaultBlockState().is(BlockTags.DIRT) || this == Blocks.FARMLAND;
		} else if (PlantType.WATER.equals(type)) {
			return state.getMaterial() == net.minecraft.world.level.material.Material.WATER; //&& state.getValue(BlockLiquidWrapper)
		} else if (PlantType.BEACH.equals(type)) {
			boolean isBeach = state.is(Blocks.GRASS_BLOCK) || ((Block)this).defaultBlockState().is(BlockTags.DIRT) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
			boolean hasWater = false;
			for (Direction face : Direction.Plane.HORIZONTAL) {
				BlockState blockState = world.getBlockState(pos.relative(face));
				net.minecraft.world.level.material.FluidState fluidState = world.getFluidState(pos.relative(face));
				hasWater |= blockState.is(Blocks.FROSTED_ICE);
				hasWater |= fluidState.is(net.minecraft.tags.FluidTags.WATER);
				if (hasWater)
					break; //No point continuing.
			}
			return isBeach && hasWater;
		}
		return false;
	}
}
