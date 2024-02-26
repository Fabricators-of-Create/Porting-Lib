package io.github.fabricators_of_create.porting_lib.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface IPlantable {
	default PlantType getPlantType(BlockGetter world, BlockPos pos) {
		if (this instanceof CropBlock) return PlantType.CROP;
		if (this instanceof SaplingBlock) return PlantType.PLAINS;
		if (this instanceof FlowerBlock) return PlantType.PLAINS;
		if (this == Blocks.DEAD_BUSH) return PlantType.DESERT;
		if (this == Blocks.LILY_PAD) return PlantType.WATER;
		if (this == Blocks.RED_MUSHROOM) return PlantType.CAVE;
		if (this == Blocks.BROWN_MUSHROOM) return PlantType.CAVE;
		if (this == Blocks.NETHER_WART) return PlantType.NETHER;
		if (this == Blocks.TALL_GRASS) return PlantType.PLAINS;
		return PlantType.PLAINS;
	}

	default BlockState getPlant(BlockGetter world, BlockPos pos) {
		return null;
	}
}
