package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.VoxelSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SimpleVoxelShape.class)
public interface CubeVoxelShapeAccessor {
	@Invoker("<init>")
	static SimpleVoxelShape port_lib$init(VoxelSet discreteVoxelShape) {
		throw new AssertionError("Mixin application failed!");
	}
}
