package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SaplingBlock;
import org.jetbrains.annotations.Nullable;

public class PlantUtil {
	public static boolean isPlant(Block block) {
		return getPlant(block) != null;
	}

	@Nullable
	public static BlockState getPlant(Block block) {
		if (block instanceof CropBlock || block instanceof SaplingBlock || block instanceof FlowerBlock ||
			block == Blocks.DEAD_BUSH || block == Blocks.LILY_PAD || block == Blocks.RED_MUSHROOM ||
			block == Blocks.BROWN_MUSHROOM || block == Blocks.NETHER_WART || block == Blocks.TALL_GRASS) {
			return block.getDefaultState();
		} else {
			return null;
		}
	}
}
