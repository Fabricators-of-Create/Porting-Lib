package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.util.IPlantable;
import io.github.fabricators_of_create.porting_lib.util.PlantType;
import net.minecraft.block.StemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StemBlock.class)
public abstract class StemBlockMixin implements IPlantable {
	@Override
	public PlantType getPlantType(BlockView world, BlockPos pos) {
		return PlantType.CROP;
	}
}
