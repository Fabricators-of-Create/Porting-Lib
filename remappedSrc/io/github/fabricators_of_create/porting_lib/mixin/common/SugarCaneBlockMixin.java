package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.util.IPlantable;
import io.github.fabricators_of_create.porting_lib.util.PlantType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin extends Block implements IPlantable {
	public SugarCaneBlockMixin(Settings properties) {
		super(properties);
	}

	@Override
	public PlantType getPlantType(BlockView world, BlockPos pos) {
		return PlantType.BEACH;
	}

	@Override
	public BlockState getPlant(BlockView world, BlockPos pos) {
		return getDefaultState();
	}
}
