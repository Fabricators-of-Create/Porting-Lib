package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.ArrayVoxelShape;
import net.minecraft.util.shape.VoxelSet;

@Mixin(ArrayVoxelShape.class)
public interface ArrayVoxelShapeAccessor {
	@Invoker("<init>")
	static ArrayVoxelShape port_lib$init(VoxelSet discreteVoxelShape, DoubleList doubleList, DoubleList doubleList2, DoubleList doubleList3) {
		throw new AssertionError("Mixin application failed!");
	}
}
