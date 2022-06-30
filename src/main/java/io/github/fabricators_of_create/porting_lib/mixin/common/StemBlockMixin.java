package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IPlantable;
import io.github.fabricators_of_create.porting_lib.util.PlantType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StemBlock;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(StemBlock.class)
public abstract class StemBlockMixin implements IPlantable {
	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.CROP;
	}
}
