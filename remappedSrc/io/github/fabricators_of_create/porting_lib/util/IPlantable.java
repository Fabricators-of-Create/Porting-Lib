package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface IPlantable {
	default PlantType getPlantType(BlockView world, BlockPos pos) {
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

	default BlockState getPlant(BlockView world, BlockPos pos) {
		return null;
	}
}
